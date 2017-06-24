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

package pw.phylame.commons.condition;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import pw.phylame.commons.function.Predication;

import java.util.List;

@AllArgsConstructor
class ConditionGroup<T> implements Predication<T> {
    @NonNull
    private List<? extends Predication<T>> conditions;

    @NonNull
    private Trigger trigger;

    @Override
    @SuppressWarnings("incomplete-switch")
    public boolean test(T value) {
        if (trigger == Trigger.DISABLE || conditions.isEmpty()) {
            return true;
        }
        for (Predication<T> cond : conditions) {
            boolean result = cond.test(value);
            switch (trigger) {
                case ALL: {
                    if (!result) {
                        return false;
                    }
                }
                break;
                case ANY: {
                    if (result) {
                        return true;
                    }
                }
                break;
                case NONE: {
                    if (result) {
                        return false;
                    }
                }
                break;
            }
        }
        switch (trigger) {
            case ALL:
                return true;
            case ANY:
                return false;
            case NONE:
                return true;
            default:
                return true;
        }
    }

    public enum Trigger {
        ALL, ANY, NONE, DISABLE
    }
}
