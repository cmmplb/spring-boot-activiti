package com.cmmplb.activiti.image;

import org.activiti.bpmn.model.Process;
import org.activiti.bpmn.model.*;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
public class ProcessDiagramGeneratorImpl extends DefaultProcessDiagramGenerator implements ProcessDiagramGenerator {

    /**
     * {@link DefaultProcessDiagramGenerator#generateProcessDiagram(BpmnModel, List, List, String, String, String)}
     */
    public ProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel,
                                                       List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName,
                                                       String labelFontName, String annotationFontName,
                                                       Color[] colors, Set<String> currIds) {
        if (null == highLightedActivities) {
            highLightedActivities = Collections.emptyList();
        }
        if (null == highLightedFlows) {
            highLightedFlows = Collections.emptyList();
        }

        prepareBpmnModel(bpmnModel);

        ProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, activityFontName, labelFontName, annotationFontName);

        // Draw pool shape, if process is participant in collaboration
        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            processDiagramCanvas.drawPoolOrLane(pool.getId(), pool.getName(), graphicInfo);
        }

        // Draw lanes
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane lane : process.getLanes()) {
                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
                processDiagramCanvas.drawPoolOrLane(lane.getId(), lane.getName(), graphicInfo);
            }
        }

        // 绘制活动及其序列流,这里添加了colors, currIds
        for (Process process : bpmnModel.getProcesses()) {
            List<FlowNode> flowNodeList = process.findFlowElementsOfType(FlowNode.class);
            for (FlowNode flowNode : flowNodeList) {
                drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedActivities, highLightedFlows, colors, currIds);
            }
        }

        // Draw artifacts
        for (Process process : bpmnModel.getProcesses()) {

            for (Artifact artifact : process.getArtifacts()) {
                drawArtifact(processDiagramCanvas, bpmnModel, artifact);
            }

            List<SubProcess> subProcesses = process.findFlowElementsOfType(SubProcess.class, true);
            if (subProcesses != null) {
                for (SubProcess subProcess : subProcesses) {
                    for (Artifact subProcessArtifact : subProcess.getArtifacts()) {
                        drawArtifact(processDiagramCanvas, bpmnModel, subProcessArtifact);
                    }
                }
            }
        }

        return processDiagramCanvas;
    }

    /**
     * {@link DefaultProcessDiagramGenerator#drawActivity(DefaultProcessDiagramCanvas, BpmnModel, FlowNode, List, List)}
     */
    protected void drawActivity(ProcessDiagramCanvas processDiagramCanvas,
                                BpmnModel bpmnModel,
                                FlowNode flowNode,
                                List<String> highLightedActivities,
                                List<String> highLightedFlows,
                                Color[] colors,
                                Set<String> currIds) {
        ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(flowNode.getClass());
        if (drawInstruction != null) {

            drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);

            // Gather info on the multi instance marker
            boolean multiInstanceSequential = false,
                    multiInstanceParallel = false,
                    collapsed = false;

            if (flowNode instanceof Activity) {
                Activity activity = (Activity) flowNode;
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
                if (multiInstanceLoopCharacteristics != null) {
                    multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
                    multiInstanceParallel = !multiInstanceSequential;
                }
            }

            // Gather info on the collapsed marker
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
            if (flowNode instanceof SubProcess) {
                collapsed = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
            } else if (flowNode instanceof CallActivity) {
                collapsed = true;
            }

            // if (scaleFactor == 1.0) {
            // Actually draw the markers
            processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(),
                    multiInstanceSequential, multiInstanceParallel, collapsed);
            // }

            // Draw highlighted activities
            // historicActivityInstance里取得的list中，最后一个节点就是当前节点，前面的节点都是已完成的节点
            if (highLightedActivities.contains(flowNode.getId())) {
                if (!CollectionUtils.isEmpty(currIds) && currIds.contains(flowNode.getId()) && !(flowNode instanceof Gateway)) {
                    // 非结束节点，并且是当前节点
                    drawHighLight((flowNode instanceof StartEvent), processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()), colors[1]);
                } else {
                    // 普通节点
                    drawHighLight((flowNode instanceof StartEvent) || (flowNode instanceof EndEvent), processDiagramCanvas, bpmnModel.getGraphicInfo(flowNode.getId()), colors[0]);
                }
            }

        }

        // Outgoing transitions of activity
        for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
            String flowId = sequenceFlow.getId();
            boolean highLighted = (highLightedFlows.contains(flowId));
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            boolean isDefault = false;
            if (defaultFlow != null && defaultFlow.equalsIgnoreCase(flowId)) {
                isDefault = true;
            }
            boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && !(flowNode instanceof Gateway);

            String sourceRef = sequenceFlow.getSourceRef();
            String targetRef = sequenceFlow.getTargetRef();
            FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
            FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(flowId);
            if (graphicInfoList != null && graphicInfoList.size() > 0) {
                graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
                int[] xPoints = new int[graphicInfoList.size()];
                int[] yPoints = new int[graphicInfoList.size()];

                for (int i = 1; i < graphicInfoList.size(); i++) {
                    GraphicInfo graphicInfo = graphicInfoList.get(i);
                    GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

                    if (i == 1) {
                        xPoints[0] = (int) previousGraphicInfo.getX();
                        yPoints[0] = (int) previousGraphicInfo.getY();
                    }
                    xPoints[i] = (int) graphicInfo.getX();
                    yPoints[i] = (int) graphicInfo.getY();

                }
                // 画高亮线
                // processDiagramCanvas.drawSequenceflow(xPoints, yPoints, false, isDefault, highLighted, colors[0]);
                processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted, colors[0]);

                // 绘制序列流标签
                GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(flowId);
                if (labelGraphicInfo != null) {
                    processDiagramCanvas.drawLabel(sequenceFlow.getName(), labelGraphicInfo, false);
                } else {
                    // 解决流程图连线名称不显示的BUG
                    GraphicInfo lineCenter = getLineCenter(graphicInfoList);
                    processDiagramCanvas.drawLabel(highLighted, sequenceFlow.getName(), lineCenter, Math.abs(xPoints[1] - xPoints[0]) >= 5);
                }
            }
        }

        // Nested elements
        if (flowNode instanceof FlowElementsContainer) {
            for (FlowElement nestedFlowElement : ((FlowElementsContainer) flowNode).getFlowElements()) {
                if (nestedFlowElement instanceof FlowNode) {
                    drawActivity(processDiagramCanvas,
                            bpmnModel,
                            (FlowNode) nestedFlowElement,
                            highLightedActivities,
                            highLightedFlows);
                }
            }
        }
    }

    protected void drawHighLight(boolean isStartOrEnd, ProcessDiagramCanvas
            processDiagramCanvas, GraphicInfo graphicInfo, Color color) {
        processDiagramCanvas.drawHighLight(isStartOrEnd, (int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(), color);
    }

    /**
     * 拷贝DefaultProcessDiagramCanvas方法
     * @return {@link org.activiti.image.impl.DefaultProcessDiagramGenerator#initProcessDiagramCanvas
     */
    protected static ProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel,
                                                                   String activityFontName,
                                                                   String labelFontName,
                                                                   String annotationFontName) {

        // 我们需要计算最大值，以了解图像整体的大小
        double minX = Double.MAX_VALUE;
        double maxX = 0;
        double minY = Double.MAX_VALUE;
        double maxY = 0;

        for (Pool pool : bpmnModel.getPools()) {
            GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
            minX = graphicInfo.getX();
            maxX = graphicInfo.getX() + graphicInfo.getWidth();
            minY = graphicInfo.getY();
            maxY = graphicInfo.getY() + graphicInfo.getHeight();
        }

        List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
        for (FlowNode flowNode : flowNodes) {

            GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());

            // width
            if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
                maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
            }
            if (flowNodeGraphicInfo.getX() < minX) {
                minX = flowNodeGraphicInfo.getX();
            }
            // height
            if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
                maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
            }
            if (flowNodeGraphicInfo.getY() < minY) {
                minY = flowNodeGraphicInfo.getY();
            }

            for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
                List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
                if (graphicInfoList != null) {
                    for (GraphicInfo graphicInfo : graphicInfoList) {
                        // width
                        if (graphicInfo.getX() > maxX) {
                            maxX = graphicInfo.getX();
                        }
                        if (graphicInfo.getX() < minX) {
                            minX = graphicInfo.getX();
                        }
                        // height
                        if (graphicInfo.getY() > maxY) {
                            maxY = graphicInfo.getY();
                        }
                        if (graphicInfo.getY() < minY) {
                            minY = graphicInfo.getY();
                        }
                    }
                }
            }
        }

        List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
        for (Artifact artifact : artifacts) {

            GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());

            if (artifactGraphicInfo != null) {
                // width
                if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
                    maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
                }
                if (artifactGraphicInfo.getX() < minX) {
                    minX = artifactGraphicInfo.getX();
                }
                // height
                if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
                    maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
                }
                if (artifactGraphicInfo.getY() < minY) {
                    minY = artifactGraphicInfo.getY();
                }
            }

            List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
            if (graphicInfoList != null) {
                for (GraphicInfo graphicInfo : graphicInfoList) {
                    // width
                    if (graphicInfo.getX() > maxX) {
                        maxX = graphicInfo.getX();
                    }
                    if (graphicInfo.getX() < minX) {
                        minX = graphicInfo.getX();
                    }
                    // height
                    if (graphicInfo.getY() > maxY) {
                        maxY = graphicInfo.getY();
                    }
                    if (graphicInfo.getY() < minY) {
                        minY = graphicInfo.getY();
                    }
                }
            }
        }

        int nrOfLanes = 0;
        for (Process process : bpmnModel.getProcesses()) {
            for (Lane l : process.getLanes()) {

                nrOfLanes++;

                GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
                // // width
                if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
                    maxX = graphicInfo.getX() + graphicInfo.getWidth();
                }
                if (graphicInfo.getX() < minX) {
                    minX = graphicInfo.getX();
                }
                // height
                if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
                    maxY = graphicInfo.getY() + graphicInfo.getHeight();
                }
                if (graphicInfo.getY() < minY) {
                    minY = graphicInfo.getY();
                }
            }
        }

        // Special case, see https://activiti.atlassian.net/browse/ACT-1431
        if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
            // Nothing to show
            minX = 0;
            minY = 0;
        }

        return new ProcessDiagramCanvas(
                (int) maxX + 10,
                (int) maxY + 10,
                (int) minX,
                (int) minY,
                activityFontName,
                labelFontName,
                annotationFontName);
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, List<String> highLightedActivities,
                                       List<String> highLightedFlows, String activityFontName, String labelFontName, String
                                               annotationFontName,
                                       Color[] colors, Set<String> currIds) {
        ProcessDiagramCanvas processDiagramCanvas = generateProcessDiagram(bpmnModel, highLightedActivities, highLightedFlows,
                activityFontName, labelFontName, annotationFontName, colors, currIds);
        return processDiagramCanvas.generateImage();
    }

    @Override
    public InputStream generateDiagram(BpmnModel bpmnModel, String activityFontName, String labelFontName, String
            annotationFontName) {
        return generateDiagram(bpmnModel, Collections.<String>emptyList(), Collections.<String>emptyList(),
                activityFontName, labelFontName, annotationFontName, new Color[]{Color.BLACK, Color.BLACK}, null);
    }

}