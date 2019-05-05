package com.pinyougou.common.util;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbItemCat;
import com.pinyougou.pojo.TbSpecification;
import com.pinyougou.pojo.TbTypeTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xie
 * @date 2019/4/27 17:53
 */
public class ExcelUtils {
    /**
     * 品牌导出
     * @param brands
     */
    public static void exportBrand( List<TbBrand> brands ) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (TbBrand brand : brands) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("品牌ID", brand.getId());
            map.put("品牌名称",brand.getName());
            map.put("品牌首字母",brand.getFirstChar());
            List<Map<String, Object>> maps = CollUtil.newArrayList(map);
            rows.addAll(maps);
        }
        ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\61952\\Desktop\\品牌管理.xls");
        writer.write(rows);
        writer.close();
    }

    /**
     * 品牌导入
     * @param file
     * @return
     * @throws IOException
     */
    public static  List<TbBrand> importBrand(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("品牌ID", "id")
                .addHeaderAlias("品牌名称", "name")
                .addHeaderAlias("品牌首字母", "firstChar");
        return reader.readAll(TbBrand.class);
    }

    /**
     * 商品分类导出
     * @param itemCats
     */
    public static void exportItemCat( List<TbItemCat> itemCats ){
        List<Map<String, Object>> rows = new ArrayList<>();
        for (TbItemCat itemCat : itemCats) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("分类ID", itemCat.getId());
            map.put("分类名称",itemCat.getName());
            map.put("类型模板ID",itemCat.getTypeId());
            List<Map<String, Object>> maps = CollUtil.newArrayList(map);
            rows.addAll(maps);
        }
        ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\61952\\Desktop\\分类管理.xls");
        writer.write(rows);
        writer.close();
    }

    /**
     * 商品分类导入
     * @param file
     * @return
     * @throws IOException
     */
    public static List<TbItemCat> importItemCat(MultipartFile file) throws IOException{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("分类ID", "id")
                .addHeaderAlias("分类名称", "name")
                .addHeaderAlias("类型模板ID", "typeId");
        return reader.readAll(TbItemCat.class);
    }
    /**
     * 规格导出
     * @param specifications
     */
    public static void exportSpecification( List<TbSpecification> specifications ){
        List<Map<String, Object>> rows = new ArrayList<>();
        for (TbSpecification specification : specifications) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("规格ID", specification.getId());
            map.put("规格名称",specification.getSpecName());
            List<Map<String, Object>> maps = CollUtil.newArrayList(map);
            rows.addAll(maps);
        }
        ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\61952\\Desktop\\规格管理.xls");
        writer.write(rows);
        writer.close();
    }

    /**
     * 规格导入
     * @param file
     * @return
     * @throws IOException
     */
    public static List<TbSpecification> importSpecification(MultipartFile file) throws IOException{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("规格ID", "id")
                .addHeaderAlias("规格名称", "specName");
        return reader.readAll(TbSpecification.class);
    }
    /**
     * 模板导出
     * @param typeTemplates
     */
     public static void exportTypeTemplate(List<TbTypeTemplate> typeTemplates){
        List<Map<String, Object>> rows = new ArrayList<>();
         for (TbTypeTemplate typeTemplate : typeTemplates) {
             Map<String, Object> map = new LinkedHashMap<>();
             map.put("模板ID", typeTemplate.getId());
             map.put("分类模板名称",typeTemplate.getName());
             map.put("关联品牌",typeTemplate.getBrandIds());
             map.put("关联规格",typeTemplate.getSpecIds());
             map.put("扩展属性",typeTemplate.getCustomAttributeItems());
             List<Map<String, Object>> maps = CollUtil.newArrayList(map);
             rows.addAll(maps);
         }
         ExcelWriter writer = ExcelUtil.getWriter("C:\\Users\\61952\\Desktop\\模板管理.xls");
         writer.write(rows);
         writer.close();
    }

    /**
     * 模板导入
     * @param file
     * @return
     * @throws IOException
     */
    public static List<TbTypeTemplate> importTypeTemplate(MultipartFile file) throws IOException{
        InputStream inputStream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(inputStream);
        reader.addHeaderAlias("模板ID", "id")
                .addHeaderAlias("分类模板名称", "name")
                .addHeaderAlias("关联品牌", "brandIds")
                .addHeaderAlias("关联规格", "specIds")
                .addHeaderAlias("扩展属性", "customAttributeItems");
        return reader.readAll(TbTypeTemplate.class);
    }

}
