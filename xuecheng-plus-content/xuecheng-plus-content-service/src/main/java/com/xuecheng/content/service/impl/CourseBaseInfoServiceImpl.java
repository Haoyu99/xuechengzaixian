package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.exception.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.CourseBaseMapper;
import com.xuecheng.content.mapper.CourseCategoryMapper;
import com.xuecheng.content.mapper.CourseMarketMapper;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.CourseBase;
import com.xuecheng.content.model.po.CourseCategory;
import com.xuecheng.content.model.po.CourseMarket;
import com.xuecheng.content.service.CourseBaseInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/***
 * @title CourseBaseInfoServiceImpl
 * @description 课程信息管理业务接口实现类
 * @author haoyu99
 * @version 1.0.0
 * @creat 2023/2/6 16:11
 **/
@Slf4j
@Service
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Autowired
    CourseBaseMapper courseBaseMapper;
    @Autowired
    CourseMarketMapper courseMarketMapper;

    @Autowired
    CourseCategoryMapper courseCategoryMapper;

    @Autowired
    CourseMarketServiceImpl courseMarketService;

    @Override
    /**
     * 实现课程查询功能
     * @author haoyu99
     * @date 2023/2/6 16:12
     * @param pageParams  分页条件
     * @param queryCourseParamsDto  查询条件
     * @return PageResult<CourseBase>
     */

    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {
        // <E extends IPage<T>> E selectPage(E page 分页参数, @Param("ew") Wrapper<T> queryWrapper 查询条件);
        //  courseBaseMapper.selectPage();


        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件，根据课程名称模糊查询
        // like(boolean condition 条件 不为空, R column 列 名字, Object val ，查询条件里的名字);
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //构建查询条件，根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //构建查询条件，根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());
        //分页查询
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        //数据
        List<CourseBase> list = pageResult.getRecords();

        //总记录数
        long total = pageResult.getTotal();

        //准备返回数据      public PageResult(List<T> items, long counts, long page, long pageSize) {
        PageResult<CourseBase> courseBasePageResult = new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

        return courseBasePageResult;
    }

    @Override
    @Transactional
    public CourseBaseInfoDto createCourseBase(Long companyID, AddCourseDto dto) {
        // 新增操作
        // 对参数合法性校验
//        if (StringUtils.isBlank(dto.getName())) {
////            throw new RuntimeException("课程名称为空");
//            XueChengPlusException.cast("课程名称为空");
//        }
//        if (StringUtils.isBlank(dto.getMt())) {
////            throw new RuntimeException("课程分类为空");
//            XueChengPlusException.cast("课程分类为空");
//
//        }
//        if (StringUtils.isBlank(dto.getSt())) {
////            throw new RuntimeException("课程分类为空");
//            XueChengPlusException.cast("课程分类为空");
//
//        }
//        if (StringUtils.isBlank(dto.getGrade())) {
////            throw new RuntimeException("课程等级为空");
//            XueChengPlusException.cast("课程等级为空");
//
//        }
//        if (StringUtils.isBlank(dto.getTeachmode())) {
////            throw new RuntimeException("教育模式为空");
//            XueChengPlusException.cast("教育模式为空");
//
//        }
//        if (StringUtils.isBlank(dto.getUsers())) {
////            throw new RuntimeException("适应人群为空");
//            XueChengPlusException.cast("适应人群为空");
//        }
//        if (StringUtils.isBlank(dto.getCharge())) {
////            throw new RuntimeException("收费规则为空");
//            XueChengPlusException.cast("收费规则为空");
//
//        }

        // 对数据进行封装，调用mapper持久化  CourseBase courseMarket 都要插入数据
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象 （将dto中和courseBae属性名一样的属性拷贝到courseBaseNew）
        BeanUtils.copyProperties(dto, courseBaseNew);
        //dto内没有的内容
        //设置审核状态 默认未提交
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态  默认未发布
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyID);
        //添加时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        Long courseId = courseBaseNew.getId();
        //新增课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarketNew);
        courseMarketNew.setId(courseId);
        //收费规则
        int i = saveCourseMarket(courseMarketNew);
//        String charge = dto.getCharge();
//        //收费课程必须写价格且价格大于0
//        if (charge.equals("201001")) {
//            Float price = dto.getPrice();
//            if (price == null || price.floatValue() <= 0) {
////                throw new RuntimeException("课程设置了收费价格不能为空且必须大于0");
//                XueChengPlusException.cast("课程设置了收费价格不能为空且必须大于0");
//
//            }
//        }
        //插入课程营销信息
        if (insert <= 0 || i <= 0) {
//            throw new RuntimeException("新增课程基本信息失败");
            XueChengPlusException.cast("新增课程基本信息失败");

        }
        //添加成功
        //返回添加的课程信息
        return getCourseBaseInfo(courseId);
    }

    /**
     * 根据课程id查询 课程基本信息和营销信息
     *
     * @param courseId 课程id
     * @return CourseBaseInfoDto 课程信息
     * @author haoyu99
     * @date 2023/2/7 11:14
     */


    @Override
    public CourseBaseInfoDto getCourseBaseInfo(Long courseId) {
        //基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        //营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        //新建返回对象
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();

        if (courseBase == null) {
            return null;
        }
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        if (courseMarket != null) {
            BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        }
        //根据课程分类编号查到课程名称
        String mt = courseBaseInfoDto.getMt();
        String st = courseBaseInfoDto.getSt();
        CourseCategory mtCategory = courseCategoryMapper.selectById(mt);
        CourseCategory stCategory = courseCategoryMapper.selectById(st);
        if (mtCategory != null) {
            String mtName = mtCategory.getName();
            courseBaseInfoDto.setMtName(mtName);
        }
        if (stCategory != null) {
            String stName = stCategory.getName();
            courseBaseInfoDto.setStName(stName);
        }
        return courseBaseInfoDto;
    }

    /**
     * 更新课程信息
     *
     * @param companyId
     * @param dto
     * @return CourseBaseInfoDto
     * @author haoyu99
     * @date 2023/2/7 14:49
     */

    @Override
    @Transactional
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        //校验 Controller控制
        Long id = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(id);
        //校验是否是本公司课程
        if (!companyId.equals(courseBase.getCompanyId())) {
            XueChengPlusException.cast("只允许修改本机构的课程");
        }

        if (courseBase == null)
            XueChengPlusException.cast("课程不存在");

        //封装基本信息数据
        BeanUtils.copyProperties(dto, courseBase);
        //更新
        courseBase.setChangeDate(LocalDateTime.now());
        int i = courseBaseMapper.updateById(courseBase);


        //封装营销信息数据(有则更新,没有则添加)

        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        saveCourseMarket(courseMarket);
        //返回
        return getCourseBaseInfo(id);
    }

    /**
     * 抽取对营销信息的校验以及保存
     *
     * @param courseMarket
     * @return int
     * @author haoyu99
     * @date 2023/2/7 15:04
     */

    private int saveCourseMarket(CourseMarket courseMarket) {
        //收费课程必须写价格且价格大于0
        String charge = courseMarket.getCharge();
        if (StringUtils.isBlank(charge)) {
            XueChengPlusException.cast("请设置收费标准");
        }
        if (charge.equals("201001")) {
            Float price = courseMarket.getPrice();
            if (price == null || price.floatValue() <= 0) {
                XueChengPlusException.cast("课程设置了收费价格不能为空且必须大于0");
            }
        }
        boolean b = courseMarketService.saveOrUpdate(courseMarket);
        return b ? 1 : -1;
    }
}
