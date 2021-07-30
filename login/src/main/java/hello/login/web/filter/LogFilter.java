package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

/**
 * 공통 관심사는 스프링의 AOP로도 해결할 수 있지만,
 * 웹과 관련된 공통 관심사는 서블릿 필터 또는 스프링 인터셉터를 사용하는 것이 좋다.
 * 웹과 관련된 공통 관심사를 처리할 때는 HTTP의 헤더나 URL의 정보들이 필요한데,
 * 서블릿 필터나 스프링 인터셉터는 HttpServletRequest 를 제공
 *
 * HTTP 요청 ->WAS-> 필터 -> 서블릿 -> 컨트롤러
 * HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러 //로그인 사용자
 * HTTP 요청 -> WAS -> 필터(적절하지 않은 요청이라 판단, 서블릿 호출X) //비 로그인 사용자
 */
@Slf4j
public class LogFilter implements Filter {

    /**
     * init(): 필터 초기화 메서드, 서블릿 컨테이너가 생성될 때 호출된다.
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    /**
     * doFilter(): 고객의 요청이 올 때 마다 해당 메서드가 호출된다. 필터의 로직을 구현하면 된다.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();

        try {
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            //다음 filter 가 있으면 다음 filter 호출, 없으면 servlet 호출,
            //만약 이 로직을 호출하지 않으면 다음 단계로 진행되지 않는다. -> http 호출이 진행되지 않음.. -> 먹통
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    /**
     * destroy(): 필터 종료 메서드, 서블릿 컨테이너가 종료될 때 호출된다.
     */
    @Override
    public void destroy() {
        log.info("log filter init");
    }
}
