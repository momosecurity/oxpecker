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
package com.thecastle.idea.component;

import com.thecastle.idea.until.InformationExtractAndSendUtil;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import org.jetbrains.annotations.NotNull;

/**
 * User: thecastle <https://github.com/IIComing>
 * Date: 2019/5/9
 * Time: 下午6:43
 */

public class OxpeckerProjectComponent implements ProjectComponent {
    private final Project project;
    private final String name = "OxpeckerProjectComponent";
    private VirtualFileListener virtualFileListener;

    public OxpeckerProjectComponent(final Project project) {
        this.project = project;
    }

    /**
     *
     */
    @Override
    public void initComponent() {
        this.virtualFileListener = new VirtualFileListener() {
            @Override
            public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {

            }

            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                if (project.isDisposed()) {
                    return;
                }

                if (!event.getFile().getPath().endsWith(".git/HEAD")) {
                    return;
                }
                //分支切换
                InformationExtractAndSendUtil.send(project, "ChangeBranch");
            }

            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {

            }

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent event) {

            }

            @Override
            public void fileMoved(@NotNull VirtualFileMoveEvent event) {

            }

            @Override
            public void fileCopied(@NotNull VirtualFileCopyEvent event) {

            }

            @Override
            public void beforePropertyChange(@NotNull VirtualFilePropertyEvent event) {

            }

            @Override
            public void beforeContentsChange(@NotNull VirtualFileEvent event) {

            }

            @Override
            public void beforeFileDeletion(@NotNull VirtualFileEvent event) {

            }

            @Override
            public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {

            }
        };
    }

    @Override
    public void disposeComponent() {

    }

    /**
     * project 打开
     */
    @Override
    public void projectOpened() {
        VirtualFileManager.getInstance().addVirtualFileListener(this.virtualFileListener);
        InformationExtractAndSendUtil.send(project, "ProjectOpen");
    }

    /**
     * project 关闭
     */
    @Override
    public void projectClosed() {
        InformationExtractAndSendUtil.send(project, "ProjectClose");
    }

    @NotNull
    @Override
    public String getComponentName() {
        return this.name;
    }
}
