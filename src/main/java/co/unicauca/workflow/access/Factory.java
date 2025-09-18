package co.unicauca.workflow.access;

public class Factory {

    private static Factory instance;

    private Factory() { }

    public static Factory getInstance() {
        if (instance == null) {
            instance = new Factory();
        }
        return instance;
    }

    public IUsersRepository getUserRepository(String type) {
        IUsersRepository result = null;

        switch (type.toLowerCase()) {
            case "sqlite":
                result = new UserSQLiteRepository();
                break;
            default:
                System.out.println("❌ Tipo de repositorio de usuarios no soportado: " + type);
                break;
        }
        return result;
    }

    public IDegreeWorkRepository getDegreeWorkRepository(String type) {
        IDegreeWorkRepository result = null;

        switch (type.toLowerCase()) {
            case "sqlite":
                result = new DegreeWorkSQLiteRepository();
                break;
            default:
                System.out.println("❌ Tipo de repositorio de trabajos de grado no soportado: " + type);
                break;
        }
        return result;
    }
}
