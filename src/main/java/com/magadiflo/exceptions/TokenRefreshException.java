package com.magadiflo.exceptions;

//@ResponseStatus(HttpStatus.FORBIDDEN) //Según lo que comentamos esto no sería adecuado para un API REST, pero en el tutorial lo tiene anotado
public class TokenRefreshException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TokenRefreshException(String token, String message) {
        super(String.format("Failed for [%s]: %s", token, message));
    }
}
/**
 * @ResponseStatus Advertencia: al usar esta anotación en una clase de excepción, o al configurar el atributo de razón de
 * esta anotación, se usará el método HttpServletResponse.sendError.
 * Con HttpServletResponse.sendError, la respuesta se considera completa y no debe escribirse más.
 * Además, el contenedor de Servlet normalmente escribirá una página de error HTML, por lo que el uso de un
 * motivo no es adecuado para las API REST. Para tales casos, es preferible usar org.springframework.http.ResponseEntity
 * como tipo de devolución y evitar el uso de @ResponseStatus por completo.
 * Tenga en cuenta que una clase de controlador también se puede anotar con @ResponseStatus, que luego heredan todos los
 * métodos @RequestMapping y @ExceptionHandler en esa clase y sus subclases, a menos que se anule mediante una
 * declaración @ResponseStatus local en el método.
 */