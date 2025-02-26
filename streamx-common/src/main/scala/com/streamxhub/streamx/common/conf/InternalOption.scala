/*
 * Copyright 2019 The StreamX Project
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

package com.streamxhub.streamx.common.conf


/**
 *
 * Internal use of the system
 *
 * @param key          key of configuration that consistent with the spring config.
 * @param defaultValue default value of configuration that <b>should not be null</b>.
 * @param classType    the class type of value. <b>please use java class type</b>.
 * @param description  description of configuration.
 * @author Al-assad
 */
case class InternalOption(key: String,
                          defaultValue: Any,
                          classType: Class[_],
                          description: String = "") {
  InternalConfigHolder.register(this)
}
