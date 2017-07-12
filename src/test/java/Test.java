/*
 * Copyright 2017 Peng Wan <phylame@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import jclp.function.Predicate;
import jclp.io.IOUtils;
import jclp.setting.Settings;
import lombok.val;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lateray on 2017-7-11.
 */
public class Test {
    public static void main(String[] args) throws IOException {
        val settings = new Settings("!app");
        Map<String, Predicate<Object>> c = new HashMap<>();
        settings.loadDependency(IOUtils.readerFor(Test.class.getResource("/pref.dep")));
        settings.setSection("app.");
        System.out.println(settings.isEnable("log.level"));
    }
}
