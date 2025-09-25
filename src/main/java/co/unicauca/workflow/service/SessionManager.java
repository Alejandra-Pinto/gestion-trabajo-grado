package co.unicauca.workflow.service;

public class SessionManager {
    private static Object currentUser;
    private static String userType;

    public static void setCurrentUser(Object user, String type) {
        currentUser = user;
        userType = type;
    }

    public static Object getCurrentUser() {
        return currentUser;
    }
    
    public static String getUserType() {
        return userType;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void clearSession() {
        currentUser = null;
        userType = null;
    }
}