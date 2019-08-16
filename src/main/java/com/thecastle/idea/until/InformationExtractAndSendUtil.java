/*
 * Copyright 2011-2019 MOMO.
 *
 * Licensed under the BSD 3-Clause License;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thecastle.idea.until;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thecastle.idea.bean.RequestParameterBean;
import com.thecastle.idea.config.OxpeckerProjectConfig;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.impl.libraries.ProjectLibraryTable;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitUtil;
import git4idea.config.GitConfigUtil;
import git4idea.repo.GitRepository;
import git4idea.repo.GitRepositoryImpl;
import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * User: thecastle <https://github.com/IIComing>
 * Date: 2019/5/9
 * Time: 下午6:43
 */
public class InformationExtractAndSendUtil {
    private static final String key = "change_md5_key";

    /**
     * @param project 打开的项目
     * @param source  动作来源
     */
    public static void send(Project project, String source) {
        String response = HttpRequestUtil.sendGet("http://web.local.com", "");
        if (response.isEmpty()) {
            return;
        }
        try {
            RequestParameterBean requestParameterBean = setRequestParameterBean(project, source);
            if (requestParameterBean.getAddress().isEmpty()) {
                return;
            }

            String requireMD5 = getMD5Sign(requestParameterBean.getRequire() + key);
            OxpeckerProjectConfig oxpeckerProjectConfig = ServiceManager.getService(project, OxpeckerProjectConfig.class);
            if (oxpeckerProjectConfig.getCurrentRevision().equals(requestParameterBean.getCurrentRevision())
                    && oxpeckerProjectConfig.getRequire().equals(requireMD5)
            ) {
                return;
            }

            Gson gson = new Gson();
            String sign = getMD5Sign(gson.toJson(requestParameterBean) + "|" + key);
            if (sign.isEmpty()) {
                return;
            }

            sendGitInformation(project, gson.toJson(requestParameterBean), sign);
            if (!oxpeckerProjectConfig.getCurrentRevision().equals(requestParameterBean.getCurrentRevision())) {
                oxpeckerProjectConfig.setCurrentRevision(requestParameterBean.getCurrentRevision());
            }
            if (!oxpeckerProjectConfig.getRequire().equals(requireMD5)) {
                oxpeckerProjectConfig.setRequire(requireMD5);
            }

        } catch (Exception e) {
            //todo
        }
    }

    /**
     * 发送数据
     *
     * @param project         打开的项目
     * @param requestParamter 指定url的请求参数
     */
    private static void sendGitInformation(Project project, String requestParamter, String sign) {

        Task.Backgroundable task = new Task.Backgroundable(project, "Information send", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String param = String.format("param=%s&sign=%s", requestParamter, sign);
                //修改为实际上传地址
                HttpRequestUtil.sendPost("http://web.local.com", param);
            }
        };
        task.queue();
    }

    /**
     * 数据签名
     *
     * @param data 签名数据
     * @return
     */

    public static String getMD5Sign(String data) {
        String result = "";
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data.getBytes("UTF8"));
            byte sign[] = md5.digest();
            for (int i = 0; i < sign.length; i++) {
                result += Integer.toHexString((0x000000FF & sign[i]) | 0xFFFFFF00).substring(6);
            }
        } catch (Exception e) {
            //todo

        }
        return result;
    }


    public static String getRequirement(Project project) {
        String require = "";
        ApplicationInfo applicationInfo = ApplicationInfo.getInstance();
        String versionName = applicationInfo.getVersionName();
        if ("IntelliJ IDEA".equalsIgnoreCase(versionName)) {
            require = getRequirementFromJava(project) + "&&&java";
        }

        if ("PhpStorm".equalsIgnoreCase(versionName)) {
            require = getRequirementFromPHP(project) + "&&&php";
        }
        return require;
    }

    /**
     * 获取maven依赖
     *
     * @param project
     * @return
     */
    public static String getRequirementFromJava(Project project) {
        Library[] libraries = ProjectLibraryTable.getInstance(project).getLibraries();

        Map<String, String> requirement = new HashMap<>();
        for (Library library : libraries) {
            String[] strings = library.getName().split(":");
            String groupId = strings[1];
            groupId = groupId.replace(" ", "");
            String artifactId = strings[2];
            String version = strings[3];
            requirement.put(groupId + ":" + artifactId, version);
        }
        return new Gson().toJson(requirement);
    }

    /**
     * 获取composer依赖
     *
     * @param project
     * @return
     */
    public static String getRequirementFromPHP(Project project) {
        VirtualFile root = project.getWorkspaceFile().getParent().getParent();
        String require = "";
        VirtualFile virtualFileComposer = root.findFileByRelativePath("composer.json");
        if (virtualFileComposer != null) {
            try {
                JsonObject jsonObject = new JsonParser().parse(new String(virtualFileComposer.contentsToByteArray())).getAsJsonObject();
                if (jsonObject != null && jsonObject.get("require") != null) {
                    require = jsonObject.get("require").toString();
                }
            } catch (Exception e) {
                //todo
            }
        }
        return require;
    }

    /**
     * 获取git信息
     *
     * @param project
     * @param source
     */
    public static RequestParameterBean setRequestParameterBean(Project project, String source) {
        RequestParameterBean requestParameterBean = new RequestParameterBean();
        VirtualFile root = project.getWorkspaceFile().getParent().getParent();
        try {
            VirtualFile virtualFileGit = root.findChild(".git");
            if (virtualFileGit == null) {
                return requestParameterBean;
            }
            GitRepository gitRepository = GitRepositoryImpl.getInstance(root, virtualFileGit, project, false);
            if (gitRepository == null) {
                return requestParameterBean;
            }
            String currentBranchName = gitRepository.getCurrentBranch().getName();
            String currentRevision = gitRepository.getCurrentRevision();
            String gitAddress = GitUtil.getDefaultRemote(gitRepository.getRemotes()).getFirstUrl();
            //修改为所需的地址
            if (!gitAddress.contains("github.com")) {
                return requestParameterBean;
            }

            if (gitAddress.startsWith("http") && gitAddress.contains("@")) {
                gitAddress = gitAddress.split("://")[0] + "://" + gitAddress.split("@")[1];
            }

            String user = GitConfigUtil.getValue(project, root, "user.name") + ":" + GitConfigUtil.getValue(project, root, "user.email");

            String require = getRequirement(project);
            if (require.isEmpty()) {
                return requestParameterBean;
            }
            String[] strings = require.split("&&&");
            if ("java".equalsIgnoreCase(strings[1])) {
                requestParameterBean.setLanguage("java");
            } else if ("php".equalsIgnoreCase(strings[1])) {
                requestParameterBean.setLanguage("php");
            } else {
                return requestParameterBean;
            }
            requestParameterBean.setAddress(gitAddress);
            requestParameterBean.setCurrentBranch(currentBranchName);
            requestParameterBean.setCurrentRevision(currentRevision);
            requestParameterBean.setUser(user);
            requestParameterBean.setRequire(strings[0]);
            requestParameterBean.setSource(source);
        } catch (Exception e) {
            //todo
        }
        return requestParameterBean;
    }
}
