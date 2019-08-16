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



package com.thecastle.idea.bean;

/**
 * User: thecastle <https://github.com/IIComing>
 * Date: 2019/5/9
 * Time: 下午6:43
 */
public class RequestParameterBean {

    private String source;
    private String user;
    private String currentRevision;
    private String currentBranch;
    private String address;
    private String require;
    private String language ;

    public RequestParameterBean() {
        this.currentRevision = "";
        this.user = "";
        this.currentBranch = "";
        this.address = "";
        this.require = "";
        this.language ="";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(String currentRevision) {
        this.currentRevision = currentRevision;
    }

    public String getCurrentBranch() {
        return currentBranch;
    }

    public void setCurrentBranch(String currentBranch) {
        this.currentBranch = currentBranch;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
