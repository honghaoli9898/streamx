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

package com.streamxhub.streamx.flink.connector.clickhouse.conf

import java.util.Properties

/**
 *
 * Flink sink for Clickhouse database. Powered by Async Http Client.
 *
 * High-performance library for loading data to Clickhouse.
 *
 * It has two triggers for loading data: by timeout and by buffer size.
 *
 */
//---------------------------------------------------------------------------------------

class ClickHouseJdbcConfig(parameters: Properties) extends Serializable {

  val sinkOption: ClickHouseSinkConfigOption = ClickHouseSinkConfigOption(properties = parameters)

  val user: String = sinkOption.user.get()

  val password: String = sinkOption.password.get()

  val jdbcUrl: String = sinkOption.jdbcUrl.get()

  val driverClassName: String = sinkOption.driverClassName.get()

  val batchSize: Int = sinkOption.batchSize.get()

  val flushInterval: Long = sinkOption.flushInterval.get()

}
