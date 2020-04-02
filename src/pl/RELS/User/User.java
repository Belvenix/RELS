package pl.RELS.User;

import pl.RELS.Server;



public abstract class User {
    protected String name;
    protected String surname;
    protected String username;
    protected String password;
    protected String bankId;
    protected long userId;
    protected static Server server;

    /**
     * This is a constructor for abstract User class
     *
     * <p>
     *     This abstract class is responsible for abstraction of user. We may in the future add more classes to that,
     *     however we have now a base class for it. It will divide into seller and buyer for time being.
     * </p>
     * @param name - this is String name of the user
     * @param surname - this is String surname of the user
     * @param user - this is String username of the user
     * @param pass - this is String not hashed user password
     * @param bank - this is String bank id (credit card)
     */
    public User(String name, String surname, String user, String pass, String bank, Server server){
        this.name = name;
        this.surname = surname;
        this.username = user;
        this.password = pass;
        this.bankId = bank;
        User.server = server;
        this.userId = this.getServer().currentUserId();
    }

    //--------------------------------------------------------------------------------------------
    //----------------------------------------METHODS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    /**
     * This method will be responsible for handling the login action of user (input operations too)
     *
     * <p>
     *     In the beginning the passwords wont be hashed nor stored for simplicity. Although later on it will look
     *     for the password in some files or database and might be hashed in the future. It doesnt need any parameters
     * </p>
     *
     * @return the return of the function are giving us information what happened.
     *          If the login went great we get 1
     *          If there were invalid credentials given it will return 0
     *          Otherwise (some problem other than invalid credentials) it will return -1
     */
    public abstract int login();

    /**
     * This function will authenticate the user if valid credentials (username and password) are given
     * @param username - username given by user trying to log in
     * @param password - password given by user trying to log in
     * @return true if there were good credentials given false otherwise
     */
    protected abstract boolean authenticate(String username, String password);

    //--------------------------------------------------------------------------------------------
    //----------------------------------------SETTERS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    /**
     * Simple setter of username.
     * @param username - username given by user
     */
    protected abstract void setUsername(String username);

    /**
     * Simple setter of password. Might be restricted later.
     * @param password - password given by user
     */
    protected abstract void setPassword(String password);

    /**
     * Simple setter of bankId. Might be restricted later.
     * @param bankId - bank id (credit card) given by user
     */
    protected abstract void setBankId(String bankId);

    protected abstract void setServer(Server server);

    protected abstract void setUserId(long userId);

    //--------------------------------------------------------------------------------------------
    //----------------------------------------GETTERS---------------------------------------------
    //--------------------------------------------------------------------------------------------

    /**
     * Simple getter of username
     * @return - returns username
     */
    public abstract String getUsername();

    /**
     * Simple getter of password. Might be deleted or restricted later.
     * @return - password in string format (not hashed yet)
     */
    protected abstract String getPassword();

    /**
     * Simple getter of bank id (credit card). Might be deleted or restricted later.
     * @return - bank id (credit card) number
     */
    public abstract String getBankId();

    public abstract Server getServer();

    public abstract long getUserId();

    /**
     * This is a function that takes control of the program for the time being (until the logout or until user dont want
     *  to interact with it)
     * @return -    returns -1 if it was failed login sequence
     *              returns 0 if user decided to logout and continue with program
     *              returns 1 if user decided to logout and quit the program
     *              returns 2 if there was some error
     */
    public abstract int actionLoop();

    /**
     * This method might be transported to application itself. More of a handler of situation.
     */
    public abstract void showOffers();
}
