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

package com.streamxhub.streamx.console.core.entity;

import com.streamxhub.streamx.console.core.enums.NoticeType;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("t_message")
public class Message {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long appId;

    private Long userId;

    private String title;

    /**
     * 1) 构建失败报
     * 2) 任务监控到异常
     */
    private Integer type;

    private String context;

    private Integer readed;

    private Date createTime;

    public Message() {
    }

    public Message(Long userId, Long appId, String title, String context, NoticeType noticeType) {
        this.userId = userId;
        this.appId = appId;
        this.title = title;
        this.context = context;
        this.type = noticeType.get();
        this.createTime = new Date();
        this.readed = 0;
    }

}
