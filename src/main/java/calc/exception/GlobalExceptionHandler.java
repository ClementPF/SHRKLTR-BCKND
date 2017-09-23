package calc.exception;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author danny
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APIException.class)
    @ResponseBody
    public ErrorResponse handleAPIException(final APIException exception, final HttpServletResponse response) {
        response.setStatus(exception.getHttpStatus().value());
        return new ErrorResponse(exception.getMessage());
    }
}
