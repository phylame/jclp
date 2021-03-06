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

package jclp.log;

import lombok.Getter;

public enum Level {
    ALL(7),
    TRACE(6),
    DEBUG(5),
    INFO(4),
    WARN(3),
    ERROR(2),
    FATAL(1),
    OFF(0);

    @Getter
    private final int code;

    Level(int code) {
        this.code = code;
    }
}
