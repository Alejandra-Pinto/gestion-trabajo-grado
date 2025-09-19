package co.unicauca.workflow.access;

import co.unicauca.workflow.domain.entities.SuperAdmin;
import co.unicauca.workflow.service.AdminService;

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
                System.out.println("Tipo de repositorio de usuarios no soportado: " + type);
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
                System.out.println("Tipo de repositorio de trabajos de grado no soportado: " + type);
                break;
        }
        return result;
    }
    
    public IAdminRepository getAdminRepository(String type) {
        IAdminRepository result = null;

        switch (type.toLowerCase()) {
            case "sqlite":
                result = new AdminSQLiteRepository();
                // Crear SuperAdmin por defecto
                AdminService service = new AdminService(result);
                if (result.findByEmail("superadmin@unicauca.edu.co") == null) {
                    service.registerAdmin(new SuperAdmin(0, "Super", "Admin", "000000000","superadmin@unicauca.edu.co", "@Admin12345"));
                }
                break;
            default:
                System.out.println("Tipo de repositorio de admins no soportado: " + type);
                break;
        }
        return result;
    }
}