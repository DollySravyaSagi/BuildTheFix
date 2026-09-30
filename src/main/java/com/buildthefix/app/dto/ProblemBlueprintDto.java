package com.buildthefix.app.dto;

import java.util.List;

/**
 * DTO containing the AI-generated structured problem blueprint.
 */
public class ProblemBlueprintDto {
    private String formalTitle;
    private String targetPersona;
    private List<String> techStack;
    private List<String> coreFeatures;
    private List<String> roadmap;

    public ProblemBlueprintDto() {}

    public ProblemBlueprintDto(String formalTitle, String targetPersona, List<String> techStack, List<String> coreFeatures, List<String> roadmap) {
        this.formalTitle = formalTitle;
        this.targetPersona = targetPersona;
        this.techStack = techStack;
        this.coreFeatures = coreFeatures;
        this.roadmap = roadmap;
    }

    public String getFormalTitle() {
        return formalTitle;
    }

    public void setFormalTitle(String formalTitle) {
        this.formalTitle = formalTitle;
    }

    public String getTargetPersona() {
        return targetPersona;
    }

    public void setTargetPersona(String targetPersona) {
        this.targetPersona = targetPersona;
    }

    public List<String> getTechStack() {
        return techStack;
    }

    public void setTechStack(List<String> techStack) {
        this.techStack = techStack;
    }

    public List<String> getCoreFeatures() {
        return coreFeatures;
    }

    public void setCoreFeatures(List<String> coreFeatures) {
        this.coreFeatures = coreFeatures;
    }

    public List<String> getRoadmap() {
        return roadmap;
    }

    public void setRoadmap(List<String> roadmap) {
        this.roadmap = roadmap;
    }
}
