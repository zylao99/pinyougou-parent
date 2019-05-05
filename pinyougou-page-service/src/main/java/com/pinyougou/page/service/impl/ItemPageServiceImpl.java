package com.pinyougou.page.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbGoodsDescMapper;
import com.pinyougou.mapper.TbGoodsMapper;
import com.pinyougou.mapper.TbItemCatMapper;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.page.service.ItemPageService;
import com.pinyougou.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.page.service.impl *
 * @since 1.0
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private TbGoodsMapper goodsMapper;

    @Autowired
    private TbGoodsDescMapper goodsDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbItemMapper itemMapper;

    //数据集+ 模板=html
    @Override
    public void genHtml(Long id) {//spu
         //1.从数据库中获取数据集（商品的数据）
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //获取商品的描述信息的数据
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //2.使用freemarker的APi来实现生成静态页面

        genHTMLfromFreemarker(tbGoods,tbGoodsDesc);


    }

    @Autowired
    private FreeMarkerConfigurer configurer;
   // 数据集+ 模板=html
    private void genHTMLfromFreemarker(TbGoods tbGoods, TbGoodsDesc tbGoodsDesc) {
        try {
        /*//1.创建一个configuration对象
        Configuration configuration = new Configuration(Configuration.getVersion());

        //2.设置字符编码 和模板做在的目录
        configuration.setDefaultEncoding("utf-8");
        configuration.setDirectoryForTemplateLoading("");*/

            Configuration configuration = configurer.getConfiguration();

            //3.创建模板文件  加载模板文件
            Template template = configuration.getTemplate("item.ftl");

            //4.创建数据集
            Map model = new HashMap();

            model.put("goods",tbGoods);//相当于 request.setAttribute()
            model.put("goodsDesc",tbGoodsDesc);//相当于 request.setAttribute()

            //查询三级分类的名称
            TbItemCat tbItemCat1 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat tbItemCat2 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat tbItemCat3 = itemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

            model.put("tbItemCat1",tbItemCat1.getName());
            model.put("tbItemCat2",tbItemCat2.getName());
            model.put("tbItemCat3",tbItemCat3.getName());

            //查询SPU对应的所有的SKU的列表 数据  存储在JS中

            TbItemExample exmaple = new TbItemExample();//select * from tb_item where goodsid=1 and status=1 order by is_defualt desc
            TbItemExample.Criteria criteria = exmaple.createCriteria();
            criteria.andGoodsIdEqualTo(tbGoods.getId());
            criteria.andStatusEqualTo("1");
            exmaple.setOrderByClause("is_default desc");
            List<TbItem> tbItems = itemMapper.selectByExample(exmaple);

            model.put("skuList",tbItems);
            //5.输出到文件中
            Writer writer = new FileWriter(new File("G:\\freemarker\\"+tbGoods.getId()+".html"));

            //6.执行生成
            template.process(model,writer);

            //7.关闭流
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
