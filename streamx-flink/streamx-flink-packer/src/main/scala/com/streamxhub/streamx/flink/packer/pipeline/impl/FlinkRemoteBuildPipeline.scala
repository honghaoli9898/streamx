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

package com.streamxhub.streamx.flink.packer.pipeline.impl

import com.streamxhub.streamx.common.fs.LfsOperator
import com.streamxhub.streamx.flink.packer.maven.MavenTool
import com.streamxhub.streamx.flink.packer.pipeline._

/**
 * Building pipeline for flink standalone session mode
 *
 * @author czy006
 */
class FlinkRemoteBuildPipeline(request: FlinkRemotePerJobBuildRequest) extends BuildPipeline {

  override def pipeType: PipelineType = PipelineType.FLINK_STANDALONE

  override def offerBuildParam: FlinkRemotePerJobBuildRequest = request

  /**
   * The construction logic needs to be implemented by subclasses
   */
  @throws[Throwable] override protected def buildProcess(): ShadedBuildResponse = {
    // create workspace.
    // the sub workspace path like: APP_WORKSPACE/jobName
    if (request.skipBuild) {
      ShadedBuildResponse(request.workspace, request.customFlinkUserJar)
    } else {
      execStep(1) {
        LfsOperator.mkCleanDirs(request.workspace)
        logInfo(s"recreate building workspace: ${request.workspace}")
      }.getOrElse(throw getError.exception)
      // build flink job shaded jar
      val shadedJar =
        execStep(2) {
          val output = MavenTool.buildFatJar(request.mainClass, request.providedLibs, request.getShadedJarPath(request.workspace))
          logInfo(s"output shaded flink job jar: ${output.getAbsolutePath}")
          output
        }.getOrElse(throw getError.exception)
      ShadedBuildResponse(request.workspace, shadedJar.getAbsolutePath)
    }

  }

}

object FlinkRemoteBuildPipeline {
  def of(request: FlinkRemotePerJobBuildRequest): FlinkRemoteBuildPipeline = new FlinkRemoteBuildPipeline(request)
}


