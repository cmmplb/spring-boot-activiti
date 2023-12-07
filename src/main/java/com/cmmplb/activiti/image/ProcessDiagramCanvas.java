package com.cmmplb.activiti.image;

import org.activiti.bpmn.model.AssociationDirection;
import org.activiti.bpmn.model.GraphicInfo;
import org.activiti.image.impl.DefaultProcessDiagramCanvas;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class ProcessDiagramCanvas extends DefaultProcessDiagramCanvas {

    protected static Color LABEL_COLOR = new Color(0, 0, 0);

    /** 动态流程图颜色定义 **/
    public static final Color COLOR_NORMAL = new Color(0, 205, 0);

    public static final Color COLOR_CURRENT = new Color(255, 0, 0);

    //font
    protected String labelFontName = "宋体";

    /**
     * 移除了String imageType和ClassLoader customClassLoader
     */
    public ProcessDiagramCanvas(int width, int height, int minX, int minY, String activityFontName, String labelFontName, String annotationFontName) {
        super(width, height, minX, minY, activityFontName, labelFontName, annotationFontName);
    }

    /**
     * {@link DefaultProcessDiagramCanvas#drawHighLight(int, int, int, int)}
     */
    public void drawHighLight(boolean isStartOrEnd,
                              int x,
                              int y,
                              int width,
                              int height,
                              Color color) {
        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        // 这里是高亮的颜色
        g.setPaint(color);
        g.setStroke(MULTI_INSTANCE_STROKE);
        if (isStartOrEnd) {
            // 开始、结束节点画圆
            g.drawOval(x, y, width, height);
        } else {
            // 非开始、结束节点画圆角矩形
            RoundRectangle2D rect = new RoundRectangle2D.Double(x, y, width, height, 5, 5);
            g.draw(rect);
        }
        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    /**
     * {@link DefaultProcessDiagramCanvas#drawSequenceflow(int[], int[], boolean, boolean, boolean)}
     */
    public void drawSequenceflow(int[] xPoints,
                                 int[] yPoints,
                                 boolean conditional,
                                 boolean isDefault,
                                 boolean highLighted,
                                 Color color) {
        drawConnection(xPoints,
                yPoints,
                conditional,
                isDefault,
                "sequenceFlow",
                AssociationDirection.ONE,
                highLighted,
                color);
    }

    /**
     * {@link DefaultProcessDiagramCanvas#drawConnection(int[], int[], boolean, boolean, String, AssociationDirection, boolean)}
     */
    public void drawConnection(int[] xPoints,
                               int[] yPoints,
                               boolean conditional,
                               boolean isDefault,
                               String connectionType,
                               AssociationDirection associationDirection,
                               boolean highLighted,
                               Color color) {

        Paint originalPaint = g.getPaint();
        Stroke originalStroke = g.getStroke();

        g.setPaint(CONNECTION_COLOR);
        if ("association".equals(connectionType)) {
            g.setStroke(ASSOCIATION_STROKE);
        } else if (highLighted) {
            // 设置高亮颜色
            g.setPaint(color);
            g.setStroke(HIGHLIGHT_FLOW_STROKE);
        }

        for (int i = 1; i < xPoints.length; i++) {
            int sourceX = xPoints[i - 1];
            int sourceY = yPoints[i - 1];
            int targetX = xPoints[i];
            int targetY = yPoints[i];
            Line2D.Double line = new Line2D.Double(sourceX, sourceY, targetX, targetY);
            g.draw(line);
        }

        if (isDefault) {
            Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            drawDefaultSequenceFlowIndicator(line);
        }

        if (conditional) {
            Line2D.Double line = new Line2D.Double(xPoints[0], yPoints[0], xPoints[1], yPoints[1]);
            drawConditionalSequenceFlowIndicator(line);
        }

        if (associationDirection.equals(AssociationDirection.ONE)
                || associationDirection.equals(AssociationDirection.BOTH)) {
            Line2D.Double line = new Line2D.Double(xPoints[xPoints.length - 2], yPoints[xPoints.length - 2],
                    xPoints[xPoints.length - 1], yPoints[xPoints.length - 1]);
            drawArrowHead(line);
        }
        if (associationDirection.equals(AssociationDirection.BOTH)) {
            Line2D.Double line = new Line2D.Double(xPoints[1], yPoints[1], xPoints[0], yPoints[0]);
            drawArrowHead(line);
        }
        g.setPaint(originalPaint);
        g.setStroke(originalStroke);
    }

    /**
     * {@link DefaultProcessDiagramCanvas#drawLabel(String, GraphicInfo, boolean)}
     */
    public void drawLabel(boolean highLighted, String text, GraphicInfo graphicInfo, boolean centered) {
        float interline = 1.0f;

        // text
        if (text != null && text.length() > 0) {
            Paint originalPaint = g.getPaint();
            Font originalFont = g.getFont();
            if (highLighted) {
                g.setPaint(COLOR_NORMAL);
            } else {
                g.setPaint(LABEL_COLOR);
            }
            g.setFont(new Font(labelFontName, Font.BOLD, 10));

            int wrapWidth = 100;
            int textY = (int) graphicInfo.getY();

            AttributedString as = new AttributedString(text);
            as.addAttribute(TextAttribute.FOREGROUND, g.getPaint());
            as.addAttribute(TextAttribute.FONT, g.getFont());
            AttributedCharacterIterator aci = as.getIterator();
            FontRenderContext frc = new FontRenderContext(null, true, false);
            LineBreakMeasurer lbm = new LineBreakMeasurer(aci, frc);

            while (lbm.getPosition() < text.length()) {
                TextLayout tl = lbm.nextLayout(wrapWidth);
                textY += tl.getAscent();
                Rectangle2D bb = tl.getBounds();
                double tX = graphicInfo.getX();
                if (centered) {
                    tX += (int) (graphicInfo.getWidth() / 2 - bb.getWidth() / 2);
                }
                tl.draw(g, (float) tX, textY);
                textY += tl.getDescent() + tl.getLeading() + (interline - 1.0f) * tl.getAscent();
            }

            // restore originals
            g.setFont(originalFont);
            g.setPaint(originalPaint);
        }
    }
}