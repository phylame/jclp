
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
import jclp.log.Level;
import jclp.log.Log;
import jclp.setting.Definition;
import jclp.setting.Dependency;
import jclp.setting.PropertiesSettings;
import jclp.MiscUtils;
import lombok.val;

import java.io.FileReader;
import java.util.ArrayList;


public class Test {
    public static void main(String[] args) throws Exception {
        Log.setLevel(Level.ALL);
        val d = new ArrayList<Definition>();
        Definition def = Definition.builder()
                .key("salary")
                .type(double.class)
                .defaults(-10)
                .dependency(new Dependency("sex", new Predicate<Object>() {
                    @Override
                    public boolean test(Object arg) {
                        return "female".equals(arg);
                    }
                }))
                .build();
        d.add(def);
        try (val r = new FileReader("E:/tmp/x.txt")) {
            val ps = new PropertiesSettings(r, MiscUtils.toMap(d));
            System.out.println(ps);
            System.out.println(ps.get("salary"));
            System.out.println(ps.isEnable("salary"));
        }
        Log.t("Test", "some text {0}", 12545);
    }
}
