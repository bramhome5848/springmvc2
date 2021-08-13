package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 서블릿 예외 처리 - 오류 페이지 작동 원리
 * 서블릿은 Exception (예외)가 발생해서 서블릿 밖으로 전달되거나 또는 response.sendError() 가 호출되었을 때 설정된 오류 페이지를 찾음.
 *
 * 예외 발생 흐름
 * WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
 *
 * sendError 흐름
 * WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러 (response.sendError())
 *
 * WAS 는 해당 예외를 처리하는 오류 페이지 정보를 확인
 * new ErrorPage(RuntimeException.class, "/error-page/500")
 *
 * 예를 들어서 RuntimeException 예외가 WAS까지 전달되면,
 * WAS 는 오류 페이지 정보를 확인한다. 확인해보니
 * RuntimeException 의 오류 페이지로 /error-page/500 이 지정되어 있다.
 * WAS 는 오류 페이지를 출력하기 위해 /error-page/500 를 다시 요청한다.
 *
 * 오류 페이지 요청 흐름
 * WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/ 500) -> View
 *
 * 예외 발생과 오류 페이지 요청 흐름
 * 1. WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
 * 2. WAS `/error-page/500` 다시 요청 -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error- page/500) -> View
 *
 * 오류 정보 추가
 * WAS 는 오류 페이지를 단순히 다시 요청만 하는 것이 아니라, 오류 정보를 request 의 attribute 에 추가해서 넘겨준다.
 * 필요하면 오류 페이지에서 이렇게 전달된 오류 정보를 사용할 수 있다.
 */
@Slf4j
@Controller
public class ErrorPageController {

    //RequestDispatcher 상수로 정의되어 있음
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

    /**
     * 오류가 발생했을 때 처리할 수 있는 컨트롤러가 필요
     * 예를 들어서 RuntimeException 예외가 발생하면 errorPageEx 에서 지정한 /error-page/500 이 호출
     */
    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 404");
        printErrorInfo(request);
        return "error-page/404";
    }

    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        printErrorInfo(request);
        return "error-page/500";
    }

    //api error controller
    //Was 까지 exception 이 전달 된 후 BasicErrorController 에 의해 이곳이 호출됨
    //produces -> 클라이언트가 요청하는 HTTP Header 의 Accept 의 값이 application/json 일 때 해당 메서드가 호출
    @RequestMapping(value = "/error-page/500", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> errorPage500Api(
            HttpServletRequest request, HttpServletResponse response) {

        log.info("API errorPage 500");

        Map<String, Object> result = new HashMap<>();
        Exception ex = (Exception) request.getAttribute(ERROR_EXCEPTION);
        result.put("status", request.getAttribute(ERROR_STATUS_CODE));
        result.put("message", ex.getMessage());

        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return new ResponseEntity<>(result, HttpStatus.valueOf(statusCode));
    }

    private void printErrorInfo(HttpServletRequest request) {
        //해당 호출 때문에 Stack trace 가 한 번더 로그에 남게됨
        //request.getAttribute(ERROR_EXCEPTION).toString() 이용시 확인하기 쉬움
        log.info("ERROR_EXCEPTION: {}", request.getAttribute(ERROR_EXCEPTION));    //예외
        log.info("ERROR_EXCEPTION_TYPE: {}", request.getAttribute(ERROR_EXCEPTION_TYPE));   //예외 타입

        // ex의 경우 NestedServletException 스프링이 한번 감싸서 반환
        log.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE)); //오류 메시지
        log.info("ERROR_REQUEST_URI: {}", request.getAttribute(ERROR_REQUEST_URI)); //클라이언트 요청 URI
        log.info("ERROR_SERVLET_NAME: {}", request.getAttribute(ERROR_SERVLET_NAME));   //오류가 발생한 서블릿 이름
        log.info("ERROR_STATUS_CODE: {}", request.getAttribute(ERROR_STATUS_CODE)); //HTTP 상태 코드
        log.info("dispatchType={}", request.getDispatcherType());
    }
}

