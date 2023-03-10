package com.xuecheng.content.service.impl;

import com.alibaba.fastjson.JSON;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.content.config.MultipartSupportConfig;
import com.xuecheng.content.feignclient.MediaServiceClient;
import com.xuecheng.content.feignclient.SearchServiceClient;
import com.xuecheng.content.feignclient.model.CourseIndex;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.mapper.CoursePublishMapper;
import com.xuecheng.content.mapper.CoursePublishPreMapper;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.CoursePreviewDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.model.po.CoursePublish;
import com.xuecheng.content.model.po.CoursePublishPre;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CoursePublishService;
import com.xuecheng.content.service.TeachplanService;
import com.xuecheng.messagesdk.model.po.MqMessage;
import com.xuecheng.messagesdk.service.MqMessageService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * @title CoursePublishServiceImpl
 * @description
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/27 14:18
 **/
@Service
@Slf4j
public class CoursePublishServiceImpl implements CoursePublishService {
    @Autowired
    CourseBaseInfoService courseBaseInfoService;

    @Autowired
    TeachplanService teachplanService;

    @Autowired
    CourseBaseMapper courseBaseMapper;

    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CoursePublishPreMapper coursePublishPreMapper;

    @Autowired
    CoursePublishMapper coursePublishMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Autowired //????????????????????????
    MediaServiceClient mediaServiceClient;

    @Autowired //????????????????????????
    SearchServiceClient searchServiceClient;


    @Override
    public CoursePreviewDto getCoursePreviewInfo(Long courseId) {
        //?????????????????????????????????
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);

        //??????????????????
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);

        CoursePreviewDto coursePreviewDto = new CoursePreviewDto();
        coursePreviewDto.setCourseBase(courseBaseInfo);
        coursePreviewDto.setTeachplans(teachplanTree);
        return coursePreviewDto;

    }

    @Override
    //[{"code":"202001","desc":"???????????????"},
    // {"code":"202002","desc":"?????????"},
    // {"code":"202003","desc":"?????????"},
    // {"code":"202004","desc":"????????????"}]
    @Transactional
    public void commitAudit(Long companyId, Long courseId) {
        //????????????
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //??????????????????
        String auditStatus = courseBase.getAuditStatus();
        //???????????????????????????????????????????????????
        if ("202003".equals(auditStatus)) {
            XueChengPlusException.cast("????????????????????????????????????????????????????????????");
        }
        //??????????????????????????????????????????
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("???????????????????????????????????????");
        }
        //????????????????????????
        if (StringUtils.isEmpty(courseBase.getPic())) {
            XueChengPlusException.cast("????????????????????????????????????");
        }

        //???????????????????????????
        CoursePublishPre coursePublishPre = new CoursePublishPre();
        //???????????????????????????????????????
        CourseBaseInfoDto courseBaseInfo = courseBaseInfoService.getCourseBaseInfo(courseId);
        BeanUtils.copyProperties(courseBaseInfo, coursePublishPre);
        //??????????????????
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //??????json
        String courseMarketJson = JSON.toJSONString(courseMarket);
        //?????????????????????json??????????????????????????????
        coursePublishPre.setMarket(courseMarketJson);
        //????????????????????????
        List<TeachplanDto> teachplanTree = teachplanService.findTeachplanTree(courseId);
        if (teachplanTree.size() <= 0) {
            XueChengPlusException.cast("??????????????????????????????????????????");
        }
        //???json
        String teachplanTreeString = JSON.toJSONString(teachplanTree);
        coursePublishPre.setTeachplan(teachplanTreeString);

        //???????????????????????????,?????????
        coursePublishPre.setStatus("202003");
        //????????????id
        coursePublishPre.setCompanyId(companyId);
        //????????????
        coursePublishPre.setCreateDate(LocalDateTime.now());
        CoursePublishPre coursePublishPreUpdate = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPreUpdate == null) {
            //???????????????????????????
            coursePublishPreMapper.insert(coursePublishPre);
        } else {
            coursePublishPreMapper.updateById(coursePublishPre);
        }

        //????????????????????????????????????
        courseBase.setAuditStatus("202003");
        courseBaseMapper.updateById(courseBase);

    }

    @Override
    @Transactional
    public void publish(Long companyId, Long courseId) {
         /*
         1?????????????????????course_publish???????????????????????????????????????????????????????????????????????????

        2?????????course_base???????????????????????????????????????

        3?????????????????????????????????????????????

        4??????mq_message????????????????????????????????????????????????course_publish
        ?????????

        1????????????????????????????????????

        2????????????????????????????????????????????????
          */
        //????????????
        //????????????????????????
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if (coursePublishPre == null) {
            XueChengPlusException.cast("??????????????????????????????????????????????????????");
        }
        //??????????????????????????????????????????
        if (!coursePublishPre.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("???????????????????????????????????????");
        }
        //??????????????????
        String auditStatus = coursePublishPre.getStatus();
        //????????????????????????
        if(!"202004".equals(auditStatus)){
            XueChengPlusException.cast("????????????????????????????????????????????????");
        }

        //????????????????????????
        saveCoursePublish(courseId);

        //???????????????
        saveCoursePublishMessage(courseId);

        //????????????????????????????????????
        coursePublishPreMapper.deleteById(courseId);
    }




    /**
   * ????????????????????????
   * @author haoyu99
   * @date 2023/2/27 17:18
   * @param courseId

   */

    private void saveCoursePublish(Long courseId) {
        //????????????????????????
        //????????????????????????
        CoursePublishPre coursePublishPre = coursePublishPreMapper.selectById(courseId);
        if(coursePublishPre == null){
            XueChengPlusException.cast("???????????????????????????");
        }

        CoursePublish coursePublish = new CoursePublish();

        //???????????????????????????
        BeanUtils.copyProperties(coursePublishPre,coursePublish);
        //[{"code":"203001","desc":"?????????"},
        // {"code":"203002","desc":"?????????"},
        // {"code":"203003","desc":"??????"}]
        coursePublish.setStatus("203002");
        CoursePublish coursePublishUpdate = coursePublishMapper.selectById(courseId);
        if(coursePublishUpdate == null){
            coursePublishMapper.insert(coursePublish);
        }else{
            coursePublishMapper.updateById(coursePublish);
        }
        //????????????????????????????????????
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        courseBase.setStatus("203002");
        courseBaseMapper.updateById(courseBase);

    }


    /**
     * ???????????????
     * @author haoyu99
     * @date 2023/2/27 17:18
     * @param courseId
     */

    private void saveCoursePublishMessage(Long courseId) {
        MqMessage mqMessage = mqMessageService.addMessage("course_publish", String.valueOf(courseId), null, null);
        if(mqMessage == null ){
            XueChengPlusException.cast("????????????????????????");
        }

    }
    /**
     * ??????????????????
     * @author haoyu99
     * @date 2023/2/28 14:19
     * @param courseId
     * @return File
     */

    @Override
    public File generateCourseHtml(Long courseId) {
        //???????????????
        File htmlFile = null;
        try {
            //??????freemarker
            Configuration configuration = new Configuration(Configuration.getVersion());

            //????????????
            //?????????????????????,classpath???templates???
            //??????classpath??????
            String classpath = this.getClass().getResource("/").getPath();
            configuration.setDirectoryForTemplateLoading(new File(classpath + "/templates/"));
            //??????????????????
            configuration.setDefaultEncoding("utf-8");

            //????????????????????????
            Template template = configuration.getTemplate("course_template.ftl");

            //????????????
            CoursePreviewDto coursePreviewInfo = getCoursePreviewInfo(courseId);

            Map<String, Object> map = new HashMap<>();
            map.put("model", coursePreviewInfo);

            //?????????
            //??????1??????????????????2???????????????
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            System.out.println(content);
            //????????????????????????????????????
            InputStream inputStream = IOUtils.toInputStream(content);
            //??????????????????
            htmlFile = File.createTempFile("course","html");
            log.debug("???????????????????????????????????????:{}",htmlFile.getAbsolutePath());

            //?????????
            FileOutputStream outputStream = new FileOutputStream(htmlFile);
            IOUtils.copy(inputStream, outputStream);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return htmlFile;
    }

    /**
     * ???????????????Minio
     * @author haoyu99
     * @date 2023/2/28 14:28
     * @param courseId
     * @param file
     */

    @Override
    public void uploadCourseHtml(Long courseId, File file) {
        MultipartFile multipartFile = MultipartSupportConfig.getMultipartFile(file);
        String result = mediaServiceClient.upload(multipartFile, "course", courseId+".html");
        if(result == null){
            XueChengPlusException.cast("??????????????????");
        }
    }
  /**
   * ????????????????????????
   * @author haoyu99
   * @date 2023/3/1 12:24
   * @param courseId
   * @return Boolean
   */

    @Override
    public Boolean saveCourseIndex(Long courseId) {
        //????????????????????????
        CoursePublish coursePublish = coursePublishMapper.selectById(courseId);
        if(coursePublish == null){
            XueChengPlusException.cast("????????????????????????");
        }
        //???????????????????????????
        CourseIndex courseIndex = new CourseIndex();
        BeanUtils.copyProperties(coursePublish,courseIndex);
        //????????????????????????api???????????????????????????
        Boolean add = searchServiceClient.add(courseIndex);
        if(!add){
            XueChengPlusException.cast("??????????????????");
        }
        return add;
    }
}
