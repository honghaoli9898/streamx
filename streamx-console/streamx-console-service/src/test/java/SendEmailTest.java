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

import com.streamxhub.streamx.common.util.DateUtils;
import com.streamxhub.streamx.common.util.YarnUtils;
import com.streamxhub.streamx.console.base.util.FreemarkerUtils;
import com.streamxhub.streamx.console.core.bean.AlertTemplate;
import com.streamxhub.streamx.console.core.bean.SenderEmail;
import com.streamxhub.streamx.console.core.entity.Application;
import com.streamxhub.streamx.console.core.enums.FlinkAppState;

import freemarker.template.Template;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.junit.Before;
import org.junit.Test;

import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class SendEmailTest {

    private Template template;

    private SenderEmail senderEmail;

    @Before
    public void initConfig() throws Exception {
        this.template = FreemarkerUtils.loadTemplateFile("alert-email.ftl");
        senderEmail = new SenderEmail();
        senderEmail.setFrom("****@domain.com");
        senderEmail.setUserName("******");
        senderEmail.setPassword("******");
        senderEmail.setSmtpPort(465);
        senderEmail.setSsl(true);
        senderEmail.setSmtpHost("smtp.exmail.qq.com");
    }

    @Test
    public void alert() {
        Application application = new Application();
        application.setStartTime(new Date());
        application.setJobName("Test My Job");
        application.setAppId("1234567890");
        application.setAlertId(1);

        application.setRestartCount(5);
        application.setRestartSize(100);

        application.setCpFailureAction(1);
        application.setCpFailureRateInterval(30);
        application.setCpMaxFailureInterval(5);

        FlinkAppState appState = FlinkAppState.FAILED;

        try {
            AlertTemplate mail = getAlertBaseInfo(application);
            mail.setType(1);
            mail.setTitle("Notify: " + application.getJobName().concat(" " + appState.name()));
            mail.setStatus(appState.name());

            StringWriter writer = new StringWriter();
            Map<String, AlertTemplate> out = new HashMap<String, AlertTemplate>();
            out.put("mail", mail);

            template.process(out, writer);
            String html = writer.toString();
            System.out.println(html);
            writer.close();

            String subject = String.format("StreamX Alert: %s %s", application.getJobName(), appState.name());
            sendEmail(subject, html, "****@domain.com");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AlertTemplate getAlertBaseInfo(Application application) {
        long duration;
        if (application.getEndTime() == null) {
            duration = System.currentTimeMillis() - application.getStartTime().getTime();
        } else {
            duration = application.getEndTime().getTime() - application.getStartTime().getTime();
        }
        duration = duration / 1000 / 60;
        String format = "%s/proxy/%s/";
        String url = String.format(format, YarnUtils.getRMWebAppURL(), application.getAppId());

        AlertTemplate template = new AlertTemplate();
        template.setJobName(application.getJobName());
        template.setStartTime(DateUtils.format(application.getStartTime(), DateUtils.fullFormat(), TimeZone.getDefault()));
        template.setDuration(DateUtils.toRichTimeDuration(duration));
        template.setLink(url);
        template.setEndTime(DateUtils.format(application.getEndTime() == null ? new Date() : application.getEndTime(), DateUtils.fullFormat(), TimeZone.getDefault()));
        template.setRestart(application.isNeedRestartOnFailed());
        template.setRestartIndex(application.getRestartCount());
        template.setTotalRestart(application.getRestartSize());
        template.setCpFailureRateInterval(DateUtils.toRichTimeDuration(application.getCpFailureRateInterval()));
        template.setCpMaxFailureInterval(application.getCpMaxFailureInterval());

        return template;
    }

    private void sendEmail(String subject, String html, String... mails) throws EmailException {
        HtmlEmail htmlEmail = new HtmlEmail();
        htmlEmail.setCharset("UTF-8");
        htmlEmail.setHostName(this.senderEmail.getSmtpHost());
        htmlEmail.setAuthentication(this.senderEmail.getUserName(), this.senderEmail.getPassword());
        htmlEmail.setFrom(this.senderEmail.getFrom());

        if (this.senderEmail.isSsl()) {
            htmlEmail.setSSLOnConnect(true);
            htmlEmail.setSslSmtpPort(this.senderEmail.getSmtpPort().toString());
        } else {
            htmlEmail.setSmtpPort(this.senderEmail.getSmtpPort());
        }
        htmlEmail.setSubject(subject);
        htmlEmail.setHtmlMsg(html);
        htmlEmail.addTo(mails);
        htmlEmail.send();
    }

}
