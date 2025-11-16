package com.hayden.hap.common.formmgr.itf;

import com.hayden.hap.common.common.entity.AbstractVO;
import com.hayden.hap.common.common.exception.HDException;
import com.hayden.hap.common.form.entity.FormItemVO;
import com.hayden.hap.common.form.entity.FormVO;
import com.hayden.hap.common.formmgr.entity.CardDataVO;
import com.hayden.hap.common.formmgr.entity.FormParamVO;
import com.hayden.hap.common.formmgr.message.ReturnResult;
import com.hayden.hap.common.orgpermission.entity.OrgPermissionDTVO;
import com.hayden.hap.common.spring.service.IService;

import java.util.List;

@IService("cardFormService")
public interface ICardFormService {

	//卡片复制要改变的字段参数名
	public static final String CARD_COPY_CHANGE_PARAM = "changeitem";

	/**
	 * 更新或插入数据
	 * @param dto
	 * @return 
	 * @author zhangfeng
	 * @date 2015年11月3日
	 */
	public ReturnResult<CardDataVO> save(FormParamVO dto) throws HDException;
	
	
	/**
	 * 改变表单参数
	 * @param formParamVO 
	 * @author zhangfeng
	 * @date 2016年3月8日
	 */
	public void changeFormParamVO(FormParamVO formParamVO);
	
	/**
	 * 保存复制
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年3月8日
	 */
	public ReturnResult<CardDataVO> saveAndCopy(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 赋值默认值
	 * @return 
	 * @author zhangfeng
	 * @date 2016年4月8日
	 */
	public AbstractVO assignDefaultValue(FormParamVO formParamVO,AbstractVO vo,Long tenantid) throws HDException; 
	
	
	/**
	 * formitemHandle:(更改private 为public接口方法). <br/>
	 * date: 2016年9月23日 <br/>
	 *
	 * @modify ZhangJie
	 * @param IFormItemVOs
	 * @param editVO
	 * @param formVO
	 * @param tenantid
	 * @throws HDException
	 */
	public void formitemHandle(List<? extends FormItemVO> IFormItemVOs,AbstractVO editVO,FormVO formVO,Long tenantid) throws HDException;
	
	/**
	 * 获取修改VO
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年11月2日
	 */
	public CardDataVO getEditVO(FormParamVO formParamVO,IEditVOGetter editVOGetter, OrgPermissionDTVO dto, boolean readOnly) throws HDException;
	
	
	/**
	 * 获取添加VO
	 * @param formParamVO
	 * @return 
	 * @author zhangfeng
	 * @date 2016年11月2日
	 */
	public CardDataVO getAddVO(FormParamVO formParamVO, OrgPermissionDTVO dto) throws HDException;
	
	/**
	 * 得到复制出来新的vo
	 * @param t
	 * @return 
	 * @author zhangfeng
	 * @date 2016年12月1日
	 */
	public <T extends AbstractVO> T getCopyVO(T t);
	
	/**
	 * 为模板保存提供的一个保存方法，起一个新事物，独立回滚
	 * @throws HDException 
	 * @author liyan
	 * @date 2016年11月24日
	 */
	public ReturnResult<CardDataVO> save__RequiresNew(FormParamVO formParamVO) throws HDException;
	
	/**
	 * 卡片复制
	 * @param formParamVO
	 * @param originalVO
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2017年4月14日
	 */
	ReturnResult<CardDataVO> cardCopy(FormParamVO formParamVO, AbstractVO originalVO, Long tenantid) throws HDException;
	
	/**
	 * 获取帮助信息
	 * @param funccode
	 * @param fitemcode
	 * @return 
	 * @author zhangfeng
	 * @date 2017年10月24日
	 */
	String getHelpInfo(String funccode, String fitemcode, Long tenantid);
	
	/**
	 * 计算只读表达式
	 * @param exp
	 * @param vo
	 * @return 
	 * @author zhangfeng
	 * @throws HDException 
	 * @date 2016年6月30日
	 */
	boolean calculateReadonlyExpression(String exp, AbstractVO vo) throws HDException;
	
	/**
	 * 处理查询选择以及公式
	 * @param editVO
	 * @param formCode
	 * @param tenantid
	 * @return
	 * @throws HDException 
	 * @author zhangfeng
	 * @date 2018年4月23日
	 */
	AbstractVO code2NameForQuerySelectAndFormual(AbstractVO editVO, String formCode, Long tenantid) throws HDException;

}
