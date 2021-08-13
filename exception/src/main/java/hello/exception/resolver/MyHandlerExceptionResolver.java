package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 스프링 MVC 는 컨트롤러(핸들러) 밖으로 예외가 던져진 경우 예외를 해결하고,
 * 동작을 새로 정의할 수 있는 방법을 제공한다. 컨트롤러 밖으로 던져진 예외를 해결하고,
 * 동작 방식을 변경하고 싶으면 HandlerExceptionResolver 를 사용
 *
 * 예외처리 과정(ExceptionResolver 적용 전)
 * - 예외 발생시 postHandler 호출 x, afterCompletion 호출, was 로 예외전달, 이후 BasicErrorController 에 의해 처리
 * 예외처리 과정(ExceptionResolver 적용 후)
 * - 예외 발생시 Dispatcher servlet 단계에서 Exception handler 를 통해 예외 해결 시도,
 * - 예외 해결 될 경우 render(model) 호출, afterCompletion 호출, was 로 정상 응답
 * - 참고 : ExceptionResolver 로 예외를 해결해도 postHandle() 은 호출되지 않는다.
 */
@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request,
                                         HttpServletResponse response, Object handler, Exception ex) {

        /**
         * ExceptionResolver 가 ModelAndView 를 반환하는 이유는
         * 마치 try, catch 를 하듯이(아래 try, catch 말고..)
         * Exception 을 처리해서 정상 흐름 처럼 변경하는 것이 목적이기 때문에
         */
        try{
            if(ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");

                //예외를 response.sendError(xxx) 호출로 변경해서 서블릿에서 상태 코드에 따른 오류를 처리하도록 위임
                //이후 WAS 는 서블릿 오류 페이지를 찾아서 내부 호출,예를 들어서 스프링부트가 기본으로 설정한 /error 가 호출
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());

                //new ModelAndView() 처럼 빈 ModelAndView 를 반환하면 뷰를 렌더링 하지 않고, 정상 흐름으로 서블릿이 리턴
                //ModelAndView 지정: ModelAndView 에 View , Model 등의 정보를 지정해서 반환하면 뷰를 렌더링
                return new ModelAndView();
            }
        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        // null 을 반환하면, 다음 ExceptionResolver 를 찾아서 실행한다.
        // 만약 처리할 수 있는 ExceptionResolver 가 없으면 예외 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로(WAS) 던진다.
        return null;
    }
}
