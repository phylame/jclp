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

import jclp.cond.Conditions;
import jclp.function.Predicate;
import jclp.log.Level;
import jclp.log.Log;
import jclp.setting.Dependency;
import jclp.setting.Settings;
import jclp.setting.SettingsListener;
import lombok.val;

import java.io.IOException;

import static jclp.util.CollectionUtils.filter;
import static jclp.util.StringUtils.join;

/**
 * Created by Lateray on 2017-7-11.
 */
public class Test implements SettingsListener {
    private Settings settings = new Settings("E:/tmp/app");

    private Test() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        Log.setLevel(Level.ALL);
        Test test = new Test();
        val settings = test.settings;
        settings.addListener(test);
        val dep = new Dependency("address", Conditions.<String>isEmpty());
        settings.getDependencies().add("zip", dep);
        settings.set("age", "1");
        System.out.println(settings.isEnable("sex"));
    }

    @Override
    public void valueChanged(String key, Object oldValue, Object newValue) {
        if (settings.isEnable(key)) {
            System.out.println(key + ": " + join(", ", filter(settings.getDependencies().relationsOf(key).iterator(), new Predicate<String>() {
                @Override
                public boolean test(String arg) {
                    return settings.isEnable(arg);
                }
            })));
        }
    }

    @Override
    public void valueRemove(String key, Object value) {
        System.out.println("key = [" + key + "], value = [" + value + "]");
    }
}
