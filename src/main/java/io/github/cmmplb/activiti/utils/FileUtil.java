package io.github.cmmplb.activiti.utils;

import io.github.cmmplb.activiti.handler.exection.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author penglibo
 * @date 2024-11-01 23:37:24
 * @since jdk 1.8
 */

@Slf4j
public class FileUtil {

    /**
     * 压缩文件
     * @param zipOutputStream 压缩文件流
     * @param filename        文件名称
     * @param in              待压缩文件流
     */
    public static void toZip(ZipOutputStream zipOutputStream, String filename, InputStream in) {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(in);
            //设置压缩包内文件的名称
            zipOutputStream.putNextEntry(new ZipEntry(filename));
            int size;
            byte[] buffer = new byte[4096];
            while ((size = bis.read(buffer)) > 0) {
                zipOutputStream.write(buffer, 0, size);
            }
            zipOutputStream.closeEntry();
        } catch (Exception e) {
            throw new BusinessException("压缩文件失败");
        } finally {
            //关闭资源
            if (null != bis) {
                try {
                    bis.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    /**
     * svg 转换为 png 图片
     */
    public static byte[] svg2Png(String svgXml) throws TranscoderException, IOException {
        // 将 svg 图片转换为 png 保存
        InputStream svgStream = new ByteArrayInputStream(svgXml.getBytes(StandardCharsets.UTF_8));
        TranscoderInput input = new TranscoderInput(svgStream);
        // png 图片生成器
        PNGTranscoder transcoder = new PNGTranscoder();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        TranscoderOutput output = new TranscoderOutput(outStream);
        transcoder.transcode(input, output);
        outStream.close();
        return outStream.toByteArray();
    }
}