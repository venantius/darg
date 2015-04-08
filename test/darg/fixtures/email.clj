(ns darg.fixtures.email)

(def test-email-1
  ;; this is an example of what we actually get forwarded to us from Mailgun
  {:stripped-html "<p>Dancing tiem!!</p><p>Aint it a thing?</p>"
   :From "butts@darg.io"
   :message-headers [["Received", "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000",]
                     ["Mime-Version", "1.0"],
                     ["Content-Type", "text/plain; charset=\"ascii\""],
                     ["Subject", "Let's dance mofo"],
                     ["From", "butts@darg.io"],
                     ["To", "darg@mail.darg.io"],
                     ["Message-Id", "<20140902015129.23125.83955@darg.io>"],
                     ["Content-Transfer-Encoding", "7bit"]],
   :stripped-signature ""
   :signature "ad94075a8d99b540f4cb4aa0847eb49328bde219a67fb5639448b559a1b92102"
   :recipient "darg@mail.darg.io"
   :stripped-text "Dancing tiem!!
   Aint it a thing?"
   :Subject "Let's dance mofo"
   :Mime-Version 1.0
   :token "ee55af9ce04725e2b93ca5844b14621ac96de7e9144b21222f"
   :from "butts@darg.io"
   :Received "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000"
   :sender "butts@darg.io"
   :Message-Id "<20140902015129.23125.83955@darg.io>"
   :To "darg@mail.darg.io"
   :Content-Transfer-Encoding "7bit"
   :timestamp 1409622691
   :Content-Type "text/plain; charset=\"ascii\""
   :subject "Let's dance mofo"
   :body-plain "Dancing tiem!!
   Aint it a thing?"})

(def test-email-2
  ;; this is an example of what we actually get forwarded to us from Mailgun
  {:stripped-html "<p>Dancing tiem!!</p><p>Aint it a thing?</p><p>Reticulated Splines</p>"
   :From "savelago@gmail.com"
   :message-headers [["Received", "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000",]
                     ["Mime-Version", "1.0"],
                     ["Content-Type", "text/plain; charset=\"ascii\""],
                     ["Subject", "Send in your log for Today: September 06 2014?"],
                     ["From", "butts@darg.io"],
                     ["To", "darg@mail.darg.io"],
                     ["Message-Id", "<20140902015129.23125.83955@darg.io>"],
                     ["Content-Transfer-Encoding", "7bit"]],
   :stripped-signature ""
   :signature "ad94075a8d99b540f4cb4aa0847eb49328bde219a67fb5639448b559a1b92102"
   :recipient "darg@mail.darg.io"
   :stripped-text "Dancing tiem!!
   Aint it a thing?
   Reticulated Splines"
   :Subject "Send in your log for Today: September 06 2014?"
   :Mime-Version 1.0
   :token "ee55af9ce04725e2b93ca5844b14621ac96de7e9144b21222f"
   :from "savelago@gmail.com"
   :Received "by luna.mailgun.net with HTTP; Tue, 02 Sep 2014 01:51:29 +0000"
   :sender "savelago@gmail.com"
   :Message-Id "<20140902015129.23125.83955@darg.io>"
   :To "darg@mail.darg.io"
   :Content-Transfer-Encoding "7bit"
   :timestamp 1409622691
   :Content-Type "text/plain; charset=\"ascii\""
   :subject "Send in your log for Today: September 06 2014?"
   :body-plain "Dancing tiem!!
   Aint it a thing?
   Reticulated Splines"})

(def test-email-3
  ;; Note that a response can either have a body-plan or a body-html, or both
  {:stripped-html "okay"
   :body-html "okay"
   :From "venantius@gmail.com"
   :message-headers [["Received" "by luna.mailgun.net with HTTP; Thu, 23 Oct 2014 10:17:18 +0000"]
                     ["Content-Type" "multipart/alternative; boundary=\"1c4a180e8b5a4fd7bf41bee4cdc32b89\""],
                     ["Mime-Version", "1.0"]
                     ["Subject", "heh"]
                     ["From", "venantius@gmail.com"]
                     ["To", "darg@mail.darg.io"]
                     ["Message-Id", "<20141023101718.65454.23474@darg.io>"]]
   :stripped-signature ""
   :signature "394c3608f2f3b7ee892c954216c726704ec0443ec7248991057ed4d7af39f47b"
   :recipient "darg@mail.darg.io"
   :stripped-text "why"
   :Subject "heh"
   :Mime-Version 1.0
   :token "d5591e597dbd105fdc91eb3c925adfe6d0dc7b6e3d795ec22a"
   :from "venantius@gmail.com"
   :Received "by luna.mailgun.net with HTTP; Thu, 23 Oct 2014 10:17:18 +0000"
   :sender "postmaster@darg.io"
   :Message-Id "<20141023101718.65454.23474@darg.io>"
   :To "darg@.mail.darg.io"
   :timestamp 1414059463
   :Content-Type "multipart/alternative; boundary=\"1c4a180e8b5a4fd7bf41bee4cdc32b89\""
   :subject "heh"
   :body-plain "why"})

(def test-email-4
  ;; This is what comes back as a reply to an email sent as the following:
  ;; (darg.services.mailgun/send-message {:from "darg@darg.io" :to "venantius@gmail.com" :subject "test-email" :text "This is a starter e-mail. \n You should reply to it so that you can check out what it comes back as in the logs.w"})
  {:stripped-html "<html><body><div dir=\"ltr\">But what if I don't want to? What does something quoted look like? What does my signature look like?</div><div class=\"gmail_extra\"><br><br><br clear=\"all\"><div><br></div>-- <br><div dir=\"ltr\">W. David Jarvis<br>M: 203.918.2328</div>&#13;\n</div></body></html>"
   :body-html "<div dir=\"ltr\">But what if I don&#39;t want to? What does something quoted look like? What does my signature look like?</div><div class=\"gmail_extra\"><br><div class=\"gmail_quote\">On Thu, Oct 23, 2014 at 11:35 AM,  <span dir=\"ltr\">&lt;<a href=\"mailto:darg@mail.darg.io\" target=\"_blank\">darg@mail.darg.io</a>&gt;</span> wrote:<br><blockquote class=\"gmail_quote\" style=\"margin:0 0 0 .8ex;border-left:1px #ccc solid;padding-left:1ex\">This is a starter e-mail.<br>\n&nbsp;You should reply to it so that you can check out what it comes back as in the logs.w<br>\n</blockquote></div><br><br clear=\"all\"><div><br></div>-- <br><div dir=\"ltr\">W. David Jarvis<br>M: 203.918.2328</div>\n</div>\n"
   :From "\"W. David Jarvis\" <venantius@gmail.com>"
   :References "<20141023103523.65434.74902@darg.io>"
   :message-headers [["X-Envelope-From", "<venantius@gmail.com>"],
                     ["Received", "from mail-qc0-f170.google.com (mail-qc0-f170.google.com [209.85.216.170]) by mxa.mailgun.org with ESMTP id 5448da30.7f79243ce230-in3; Thu, 23 Oct 2014 10:36:32 -0000 (UTC)"]
                     ["Received", "by mail-qc0-f170.google.com with SMTP id l6so475290qcy.29 for <darg@mail.darg.io>; Thu, 23 Oct 2014 03:36:31 -0700 (PDT)"]
                     ["Dkim-Signature", "v=1; a=rsa-sha256; c=relaxed/relaxed; d=gmail.com; s=20120113; h=mime-version:in-reply-to:references:from:date:message-id:subject:to :content-type; bh=LQTRiwXA+958wAY4g37ctitkkooDT7ic9BdmaHzXR1Y=; b=JZGVw36GnvetCTl+jUuTXUcO+tzl7g9f7IkJVWuJJOrv1Zhj3BoBZEA2bA7cSRVXL8 6/hNfAXHtQwrzeW588+tZ6M0Sg6jOkpmPvA8Ec4nn4uJ1w97LzJNY+gRSG+2QUPh2vwr GJsVV3xL10xzuTR3Z9brgvtstrUqr0Di9s/n5TMdQ7hU8HVgP3MKh+dQUUr2LSuUChhx e4CviLpq0gOF0bW6QG+Pmfw7Tefci9isnwtXlwKAsHQjtorOiBUYYHtisR4FO9maLjg5 FkDR8O7+6vyO4HIuIUaCWEw0jej+zjiOz0OejdOziUj6aG/luiJybrgpyjfWU8QEuB+X h+hA=="]
                     ["X-Received", "by 10.229.104.3 with SMTP id m3mr6380333qco.0.1414060591154; Thu, 23 Oct 2014 03:36:31 -0700 (PDT)"]
                     ["Mime-Version", "1.0"]
                     ["Received", "by 10.140.93.1 with HTTP; Thu, 23 Oct 2014 03:36:00 -0700 (PDT)"]
                     ["In-Reply-To", "<20141023103523.65434.74902@darg.io>"]
                     ["References", "<20141023103523.65434.74902@darg.io>"]
                     ["From", "\"W. David Jarvis\" <venantius@gmail.com>"]
                     ["Date", "Thu, 23 Oct 2014 11:36:00 +0100"]
                     ["Message-Id", "<CAFMAO9xkVpd_YO4tXia4-C_9fVM7ZJ8J3R_vaniYGRvpaNTP-A@mail.gmail.com>"]
                     ["Subject", "Re: test-email"]
                     ["To", "darg@mail.darg.io"]
                     ["Content-Type", "multipart/alternative; boundary=\"001a1133322a3a0d66050614a371\""]
                     ["X-Mailgun-Incoming", "Yes"]]
   :stripped-signature "-- \nW. David Jarvis\nM: 203.918.2328"
   :X-Envelope-From "<venantius@gmail.com>"
   :Dkim-Signature "v=1; a=rsa-sha256; c=relaxed/relaxed; d=gmail.com; s=20120113; h=mime-version:in-reply-to:references:from:date:message-id:subject:to :content-type; bh=LQTRiwXA+958wAY4g37ctitkkooDT7ic9BdmaHzXR1Y=; b=JZGVw36GnvetCTl+jUuTXUcO+tzl7g9f7IkJVWuJJOrv1Zhj3BoBZEA2bA7cSRVXL8 6/hNfAXHtQwrzeW588+tZ6M0Sg6jOkpmPvA8Ec4nn4uJ1w97LzJNY+gRSG+2QUPh2vwr GJsVV3xL10xzuTR3Z9brgvtstrUqr0Di9s/n5TMdQ7hU8HVgP3MKh+dQUUr2LSuUChhx e4CviLpq0gOF0bW6QG+Pmfw7Tefci9isnwtXlwKAsHQjtorOiBUYYHtisR4FO9maLjg5 FkDR8O7+6vyO4HIuIUaCWEw0jej+zjiOz0OejdOziUj6aG/luiJybrgpyjfWU8QEuB+X h+hA=="
   :signature "5869fe2519678e8372b5df716d252d4da114e2d203f76f6a6044f90f8a14a43c"
   :recipient "darg@mail.darg.io"
   :stripped-text "But what if I don't want to? What does something quoted look like? What\ndoes my signature look like?"
   :Subject "Re: test-email"
   :Mime-Version 1.0
   :token "f128dbfee7a9ac7d14d476cbd94f98cdb50a6b07bbb7ccd9eb"
   :In-Reply-To "<20141023103523.65434.74902@darg.io>"
   :from "\"W. David Jarvis\" <venantius@gmail.com>"
   :Received ["from mail-qc0-f170.google.com (mail-qc0-f170.google.com [209.85.216.170]) by mxa.mailgun.org with ESMTP id 5448da30.7f79243ce230-in3; Thu, 23 Oct 2014 10:36:32 -0000 (UTC) by mail-qc0-f170.google.com with SMTP id l6so475290qcy.29 for <darg@mail.darg.io>; Thu, 23 Oct 2014 03:36:31 -0700 (PDT) by 10.140.93.1 with HTTP; Thu, 23 Oct 2014 03:36:00 -0700 (PDT)"]
   :Date "Thu, 23 Oct 2014 11:36:00 +0100"
   :sender "venantius@gmail.com"
   :Message-Id "<CAFMAO9xkVpd_YO4tXia4-C_9fVM7ZJ8J3R_vaniYGRvpaNTP-A@mail.gmail.com>"
   :To "darg@mail.darg.io"
   :timestamp 1414060738
   :X-Mailgun-Incoming "Yes"
   :X-Received "by 10.229.104.3 with SMTP id m3mr6380333qco.0.1414060591154; Thu, 23 Oct 2014 03:36:31 -0700 (PDT)"
   :Content-Type "multipart/alternative; boundary=\"001a1133322a3a0d66050614a371\""
   :subject "Re: test-email"
   :body-plain "But what if I don't want to? What does something quoted look like? What\ndoes my signature look like?\n\nOn Thu, Oct 23, 2014 at 11:35 AM, <darg@darg.io> wrote:\n\n> This is a starter e-mail.\n>  You should reply to it so that you can check out what it comes back as in\n> the logs.w\n>\n\n\n\n--\nW. David Jarvis\nM: 203.918.2328\n"})
