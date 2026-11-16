package ma.uir.awd.model;

/**
 * User roles for access control.
 * - ADMIN: manages users, categories, settings, ML, dashboard
 * - AGENT: manages fleet, processes pick-ups and returns
 * - CLIENT: books vehicles, sees own history and invoices
 */
public enum Role {
    ADMIN, AGENT, CLIENT
}
