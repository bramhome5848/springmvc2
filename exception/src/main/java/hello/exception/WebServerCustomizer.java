package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

//@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        //errorPage404 호출
        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        //errorPage500 호출
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");
        //RuntimeException 또는 그 자식 타입의 예외: errorPageEx 호출
        //오류 페이지는 예외를 다룰 때 해당 예외와 그 자식 타입의 오류를 함께 처리한다.
        //예를 들어서 위의 경우 RuntimeException 은 물론이고 RuntimeException 의 자식도 함께 처리한다.
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");

        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);
    }
}

/**
 * 스프링 부트는 위 코드 작성 과정을 모두 기본으로 제공
 * ErrorPage 를 자동으로 등록한다.
 * 이때 /error 라는 경로로 기본 오류 페이지를 설정한다.
 * new ErrorPage("/error") , 상태코드와 예외를 설정하지 않으면 기본 오류 페이지로 사용된다.
 * 서블릿 밖으로 예외가 발생하거나, response.sendError(...) 가 호출되면 모든 오류는 /error 를 호출하게 된다.
 * BasicErrorController 라는 스프링 컨트롤러를 자동으로 등록한다.
 * ErrorPage 에서 등록한 /error 를 매핑해서 처리하는 컨트롤러다.
 *
 * 오류가 발생했을 때 오류 페이지로 /error 를 기본 요청한다.
 * 스프링 부트가 자동 등록한 BasicErrorController 는 이 경로를 기본으로 받는다.
 *
 * 개발자는 오류 페이지만 등록
 * BasicErrorController 는 기본적인 로직이 모두 개발되어 있다.
 * 개발자는 오류 페이지 화면만 BasicErrorController 가 제공하는 룰과 우선순위에 따라서 등록하면 된다.
 * 정적 HTML 이면 정적 리소스, 뷰 템플릿을 사용해서 동적으로 오류 화면을 만들고 싶으면
 * 뷰 템플릿 경로에 오류 페이지 파일을 만들어서 넣어두기만 하면 된다.
 *
 * 뷰 선택 우선순위
 * BasicErrorController 의 처리 순서
 *
 * 1. 뷰템플릿
 * resources/templates/error/500.html
 * resources/templates/error/5xx.html
 *
 * 2. 정적리소스(static,public)
 * resources/static/error/400.html
 * resources/static/error/404.html
 * resources/static/error/4xx.html
 *
 * 3. 적용 대상이 없을 때 뷰 이름(error)
 * resources/templates/error.html
 *
 * 뷰 템플릿이 정적 리소스보다 우선순위가 높고, 404, 500처럼 구체적인 것이 5xx 처럼 덜 구체적인 것 보다 우선순위가 높다.
 */
