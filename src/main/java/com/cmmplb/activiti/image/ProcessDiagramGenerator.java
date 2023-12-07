package com.cmmplb.activiti.image;

import org.activiti.bpmn.model.BpmnModel;

import java.awt.*;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface ProcessDiagramGenerator extends org.activiti.image.ProcessDiagramGenerator {

    InputStream generateDiagram(BpmnModel bpmnModel,
                                List<String> highLightedActivities,
                                List<String> highLightedFlows,
                                String activityFontName,
                                String labelFontName,
                                String annotationFontName,
                                Color[] colors,
                                Set<String> currIds);
}
 