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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

/**
 * User: thecastle <https://github.com/IIComing>
 * Date: 2019/5/9
 * Time: 下午6:43
 */

public class HttpRequestUtil {
    /**
     * 向指定URL发送GET请求
     *
     * @param url   请求URL
     * @param param 请求参数
     * @return 响应结果
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String spliceUrl = url + "?" + param;
            URL requestUrl = new URL(spliceUrl);
            // 打开连接
            URLConnection connection = requestUrl.openConnection();
            // 设置请求属性

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "close");
            connection.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS ) Gecko/20100101 Momo/1.0");
            // 建立实际的连接
            connection.connect();
            // 读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            //todo
        } finally {
            // 使用finally块来关闭输入流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                //todo
            }
        }
        return result;
    }

    /**
     * 向指定URL发送POST请求
     *
     * @param url   请求URL
     * @param param 请求参数
     * @return 响应结果
     */
    public static String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";

        try {
            URL requestUrl = new URL(url);
            // 打开和URL的连接
            URLConnection conn = requestUrl.openConnection();
            // 设置通用请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "close");
            conn.setRequestProperty("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS ) Gecko/20100101 Momo/1.0");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数desdd
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            //todo
        } finally {
            //使用finally关闭输出流、输入流
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                //todo
            }
        }
        return result;
    }
}