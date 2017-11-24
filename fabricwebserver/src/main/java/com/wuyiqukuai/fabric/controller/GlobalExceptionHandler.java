//package com.wuyiqukuai.fabric.controller;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.servlet.ModelAndView;
//
////@ControllerAdvice
////public class GlobalExceptionHandler {
////	
//////	public static final String DEFAULT_ERROR_VIEW = "error";
////
////    @ExceptionHandler(value = Exception.class)
////    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
////        ModelAndView mv = new ModelAndView();
////        mv.addObject("exception", e);
////        mv.addObject("url", req.getRequestURL());
//////        mv.setViewName(DEFAULT_ERROR_VIEW);
////        return mv;
////    }
////
////}
