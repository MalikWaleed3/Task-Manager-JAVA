package Model;

public final class Projects {
    public static ProjectMap globalProjectList = new ProjectMap();

    private Projects(){}

    public static void addProject(Project project) {
        globalProjectList.put(project.getID(), project);
    }

    public static Project getProject(String projectID) {
        return globalProjectList.get(projectID);
    }
}
