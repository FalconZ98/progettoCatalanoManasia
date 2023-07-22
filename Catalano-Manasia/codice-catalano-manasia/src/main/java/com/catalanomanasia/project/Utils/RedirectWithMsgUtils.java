package com.catalanomanasia.project.Utils;

import jakarta.servlet.http.HttpSession;

public class RedirectWithMsgUtils {
    public static String redirectWithMsg(HttpSession session, String msg, String url, Boolean isError) {
        String result = isError ? "error" : "success";
        session.setAttribute("msg", msg);
        return "redirect:" + url + "?" + result;
    }
}
