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
package com.thecastle.idea.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;

/**
 * User: thecastle <https://github.com/IIComing>
 * Date: 2019/5/9
 * Time: 下午6:43
 */

@State(
        name = "OxpeckerProjectConfig",
        storages = {@Storage(file = "/OxpeckerProjectConfig.xml")}
)
public class OxpeckerProjectConfig implements PersistentStateComponent<OxpeckerProjectConfig> {


    private String currentRevision = "";
    private String require = "";

    public String getCurrentRevision() {
        return currentRevision;
    }

    public void setCurrentRevision(String currentRevision) {
        this.currentRevision = currentRevision;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    @Override
    public OxpeckerProjectConfig getState() {
        return this;
    }

    @Override
    public void loadState(OxpeckerProjectConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}

