package ma.uir.awd.web.action;

import org.apache.struts2.action.ServletRequestAware;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Handles user logout by invalidating the HTTP session.
 */
public class LogoutAction extends BaseAction implements ServletRequestAware {

    private HttpServletRequest httpRequest;

    @Override
    public void withServletRequest(HttpServletRequest request) {
        this.httpRequest = request;
    }

    @Override
    public String execute() {
        if (httpRequest != null) {
            HttpSession s = httpRequest.getSession(false);
            if (s != null) {
                s.invalidate();
            }
        }
        if (session != null) {
            session.clear();
        }
        return SUCCESS;
    }
}
