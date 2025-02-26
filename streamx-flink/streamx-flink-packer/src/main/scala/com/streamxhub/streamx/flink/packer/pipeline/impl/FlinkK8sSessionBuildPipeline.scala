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
import com.streamxhub.streamx.common.util.DateUtils
import com.streamxhub.streamx.common.util.DateUtils.fullCompact
import com.streamxhub.streamx.flink.packer.maven.MavenTool
import com.streamxhub.streamx.flink.packer.pipeline._

/**
 * Building pipeline for flink kubernetes-native session mode
 *
 * @author Al-assad
 */
class FlinkK8sSessionBuildPipeline(request: FlinkK8sSessionBuildRequest) extends BuildPipeline {

  override def pipeType: PipelineType = PipelineType.FLINK_NATIVE_K8S_SESSION

  override def offerBuildParam: FlinkK8sSessionBuildRequest = request

  /**
   * The construction logic needs to be implemented by subclasses
   */
  @throws[Throwable] override protected def buildProcess(): ShadedBuildResponse = {

    // create workspace.
    // the sub workspace path like: APP_WORKSPACE/k8s-clusterId@k8s-namespace/
    val buildWorkspace =
    execStep(1) {
      val buildWorkspace = s"${request.workspace}/${request.clusterId}@${request.k8sNamespace}"
      LfsOperator.mkCleanDirs(buildWorkspace)
      logInfo(s"recreate building workspace: $buildWorkspace")
      buildWorkspace
    }.getOrElse(throw getError.exception)

    // build flink job shaded jar.
    // the output shaded file name like: streamx-flinkjob_myjob_20211024134822
    val shadedJar =
    execStep(2) {
      val output = MavenTool.buildFatJar(request.mainClass, request.providedLibs, request.getShadedJarPath(buildWorkspace))
      logInfo(s"output shaded flink job jar: ${output.getAbsolutePath}")
      output
    }.getOrElse(throw getError.exception)

    ShadedBuildResponse(buildWorkspace, shadedJar.getAbsolutePath)
  }
}

object FlinkK8sSessionBuildPipeline {
  def of(request: FlinkK8sSessionBuildRequest): FlinkK8sSessionBuildPipeline = new FlinkK8sSessionBuildPipeline(request)
}
