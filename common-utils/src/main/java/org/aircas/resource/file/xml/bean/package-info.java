/**
 * JAXB:XML与JAVA对象互转
 *
 * @XmlRootElement。这是在JAXB中使用的Object必须有的注解。它定义了XML内容的根元素。
 * @XmlType。它将类映射到XML模式类型。我们可以用它来排列XML中的元素。
 * @XmlTransient。这将确保Object属性不被写入XML中。
 * @XmlAttribute: 这将创建Object属性作为一个属性。
 * @XmlElement(name = “ABC”): 这将创建名称为 "ABC "的元素。
 * @XmlElementWrapper： 注解表示生成一个包装器元素（一般用于集合元素）。
 */
package org.aircas.resource.file.xml.bean;