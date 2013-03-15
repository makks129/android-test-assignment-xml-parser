package com.example.android_test_assignment_xml_parser.model.mapping;

import com.example.android_test_assignment_xml_parser.model.mapping.annotation.POJOListClass;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SimpleXMLToPOJOMapper uses DocumentBuilder for parsing XML and a
 * recursive algorithm for mapping Document's nodes to POJO classes fields.
 *
 * Requirements:
 * - Each XML tag is a child and must have own POJO class
 * - Each child that is repeated consecutively at the same depth must be:
 * 		- mapped to ArrayList<ChildClassName>
 * 		- annotated with @POJOListClass(ChildClassName.class)
 * - Fields for attributes are only of class String
 * - elementValue (must be of class String) is a special field that
 * 	 contains text between tags in XML
 * - All POJO classes must have a no-arg constructor defined explicitly
 * - All fields must be public (no encapsulation support yet)
 * - All fields' names must perfectly match XML tag names
 *
 * Example of XML:
 * <root>
 *     <childDepthOne attributeOne="x" attributeTwo="x">
 *         <childDepthTwoFirst attributeOne="x" attributeTwo="x"/>
 *         <childDepthTwoSecond attributeOne"x" attributeTwo="x"/>
 *         <childDepthTwoThird>Text between tags</childDepthTwoThird>
 *     </childDepthOne>
 *     <childDepthOne attributeOne="x" attributeTwo="x">
 ...
 *     </childDepthOne>
 * </root>
 *
 * Example of POJO:
 * public class Pojo {
 * 	   String elementValue;
 * 	   ChildClassOne childClassOne;
 *     @POJOListClass(ChildClassTwo.class)
 *     ArrayList<ChildClassTwo> childClassTwo;
 *
 *     public RootPOJO() {}
 * }
 *
 * Author: maxim_strelnikov
 */

// TODO: add encapsulation support (and setting field values through accessing list of class's methods)
public class SimpleXMLToPOJOMapper {

	private static final String FIELD_NAME_FOR_TEXT_BETWEEN_TAGS = "elementValue";

	public static void mapXMLToPOJO(File XMLFile, Object POJOWithList) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(XMLFile);
		doc.getDocumentElement().normalize();
		Node root = doc.getDocumentElement();
		mapChildrenOfRoot(POJOWithList, root);
	}

	private static void mapChildrenOfRoot(Object rootObject, Node rootNode) throws Exception{
		ArrayList<Field> fields = new ArrayList<Field>(Arrays.asList(rootObject.getClass().getDeclaredFields()));
		NodeList nodeList = rootNode.getChildNodes();

		for (Field field : fields) {
			if (field.getName().equals(FIELD_NAME_FOR_TEXT_BETWEEN_TAGS)) {
				setFieldIfTextBetweenTags(field, nodeList, rootObject);
				continue;
			}
			if (field.getType().equals(String.class) && rootNode.hasAttributes()) {
				setFieldIfAttribute(field, rootNode, rootObject);
				continue;
			}
			if (field.isAnnotationPresent(POJOListClass.class)) {
				setFieldIfListOfChildren(field, nodeList, rootObject);
				continue;
			}
			setFieldIfChildOfOwnClass(field, nodeList, rootObject);
		}
	}

	private static void setFieldIfTextBetweenTags(Field field, NodeList nodeList, Object rootObject) throws IllegalAccessException {
		String elementValue = "";
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getLocalName() == null) {
				String nodeValue = node.getNodeValue().replaceAll("[\n\t]", "").trim();
				elementValue += nodeValue + " ";
			}
		}
		field.set(rootObject, elementValue);
	}

	private static void setFieldIfAttribute(Field field, Node rootNode, Object rootObject) throws IllegalAccessException {
		NamedNodeMap attributes = rootNode.getAttributes();
		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);
			if (field.getName().equals(attribute.getNodeName())) {
				field.set(rootObject, attribute.getNodeValue());
			}
		}
	}

	private static void setFieldIfListOfChildren(Field field, NodeList nodeList, Object rootObject) throws Exception {
		List<Object> list = (List<Object>) field.getType().newInstance();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (field.getName().equals(node.getNodeName())) {
				POJOListClass annotation = field.getAnnotation(POJOListClass.class);
				Object listChild = annotation.value().newInstance();
				mapChildrenOfRoot(listChild, node);
				list.add(listChild);
			}
		}
		field.set(rootObject, list);
	}

	private static void setFieldIfChildOfOwnClass(Field field, NodeList nodeList, Object rootObject) throws Exception {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (field.getName().equals(node.getNodeName())) {
				Object child = field.getType().newInstance();
				mapChildrenOfRoot(child, node);
				field.set(rootObject, child);
			}
		}
}

}
