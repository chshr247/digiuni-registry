package reflection;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import crud.*;
import network.*;
import model.*;
import repository.*;


public class ReflectionModule {

    public static void showReflectionMenu() {
        boolean running = true;

        while (running) {
            System.out.println("""
                    
                    --- REFLECTION / ANNOTATIONS ---
                    1. Show students details
                    2. Show teachers details
                    3. Show faculties details
                    4. Show departments details
                    5. Show class metadata
                    0. Back
                    """);

            int choice = CRUD.intInRange("Your choice: ", 0, 5);

            switch (choice) {
                case 1 -> showStudentsDetails();
                case 2 -> showTeachersDetails();
                case 3 -> showFacultiesDetails();
                case 4 -> showDepartmentsDetails();
                case 5 -> showMetadataDemo();
                case 0 -> running = false;
            }
        }
    }

    private static void showStudentsDetails() {
        List<Student> students = CRUD.students.stream()
                .filter(Student.class::isInstance)
                .map(Student.class::cast)
                .toList();

        printCollection("Students", students);
    }

    private static void showTeachersDetails() {
        printCollection("Teachers", CRUDForTeacher.teachers);
    }

    private static void showFacultiesDetails() {
        printCollection("Faculties", CRUDForFaculty.faculties);
    }

    private static void showDepartmentsDetails() {
        List<Department> departments = CRUDForFaculty.faculties.stream()
                .flatMap(faculty -> faculty.getDepartments() == null
                        ? Stream.<Department>empty()
                        : faculty.getDepartments().stream())
                .toList();

        printCollection("Departments", departments);
    }

    private static void showMetadataDemo() {
        showClassMetadata(Student.class);
        showClassMetadata(Teacher.class);
        showClassMetadata(Faculty.class);
        showClassMetadata(Department.class);
    }

    private static void showClassMetadata(Class<?> clazz) {
        ReflectiveEntity annotation = clazz.getAnnotation(ReflectiveEntity.class);

        if (annotation == null) {
            System.out.println("\nClass " + clazz.getSimpleName() + " is not marked with @ReflectiveEntity");
            return;
        }

        System.out.println("\nCLASS METADATA");
        System.out.println("Entity name: " + annotation.value());
        System.out.println("Class name: " + clazz.getSimpleName());

        if (clazz.getSuperclass() != null && clazz.getSuperclass() != Object.class) {
            System.out.println("Superclass: " + clazz.getSuperclass().getSimpleName());
        }

        System.out.println("Fields:");
        for (Field field : getAllFields(clazz)) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (field.isAnnotationPresent(ReflectIgnore.class)) continue;

            System.out.println("- " + field.getName() + " : " + field.getType().getSimpleName());
        }
    }

    private static void printCollection(String title, Collection<?> items) {
        System.out.println("\n" + title.toUpperCase() + "  ");

        if (items == null || items.isEmpty()) {
            System.out.println("No data found.");
            return;
        }

        for (Object item : items) {
            printEntity(item);
        }
    }

    private static void printEntity(Object entity) {
        if (entity == null) {
            System.out.println("null");
            return;
        }

        Class<?> clazz = entity.getClass();
        ReflectiveEntity annotation = clazz.getAnnotation(ReflectiveEntity.class);

        if (annotation == null) {
            System.out.println("Class " + clazz.getSimpleName() + " is not marked with @ReflectiveEntity");
            return;
        }

        System.out.println("\n--- " + annotation.value() + " ---");

        for (Field field : getAllFields(clazz)) {
            if (Modifier.isStatic(field.getModifiers())) continue;
            if (field.isAnnotationPresent(ReflectIgnore.class)) continue;

            try {
                field.setAccessible(true);
                Object value = field.get(entity);
                System.out.println(prettyFieldName(field.getName()) + ": " + formatValue(value));
            } catch (IllegalAccessException e) {
                System.out.println(field.getName() + ": access error");
            }
        }
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        List<Class<?>> hierarchy = new ArrayList<>();
        Class<?> current = clazz;

        while (current != null && current != Object.class) {
            hierarchy.add(0, current);
            current = current.getSuperclass();
        }

        List<Field> fields = new ArrayList<>();
        for (Class<?> type : hierarchy) {
            for (Field field : type.getDeclaredFields()) {
                fields.add(field);
            }
        }

        return fields;
    }

    private static String prettyFieldName(String fieldName) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fieldName.length(); i++) {
            char ch = fieldName.charAt(i);

            if (i > 0 && Character.isUpperCase(ch)) {
                sb.append(' ');
            }

            if (i == 0) {
                sb.append(Character.toUpperCase(ch));
            } else {
                sb.append(ch);
            }
        }

        return sb.toString();
    }

    private static String formatValue(Object value) {
        if (value == null) {
            return "null";
        }

        if (value instanceof Department department) {
            return department.getFullName() + " (id=" + department.getId() + ")";
        }

        if (value instanceof Faculty faculty) {
            return faculty.getFullName() + " (id=" + faculty.getId() + ")";
        }

        if (value instanceof University university) {
            return university.getFullName();
        }

        if (value instanceof Person person) {
            return person.getFullName() + " (id=" + person.getId() + ")";
        }

        if (value instanceof Collection<?> collection) {
            return collection.size() + " item(s)";
        }

        return String.valueOf(value);
    }
}