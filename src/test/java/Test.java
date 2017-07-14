
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

import jclp.log.Level;
import jclp.log.Log;
import jclp.setting.DefinitionFactory;
import jclp.setting.PropertiesSettings;
import jclp.util.MiscUtils;
import lombok.val;

import java.io.File;
import java.io.FileReader;


public class Test {
    public static void main(String[] args) throws Exception {
        Log.setLevel(Level.ALL);

        val definitions = DefinitionFactory.forJSON(new File("E:/tmp/x.txt.def"));
        System.out.println(definitions);
        try (val r = new FileReader("E:/tmp/x.txt")) {
            val ps = new PropertiesSettings(r, MiscUtils.toMap(definitions));
            System.out.println(ps.get("salary"));
        }
    }
}
