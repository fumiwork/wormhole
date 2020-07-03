/*-
 * <<
 * wormhole
 * ==
 * Copyright (C) 2016 - 2018 EDP
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * >>
 */

package edp.wormhole.flinkextension.pattern

import edp.wormhole.util.DateUtils.{dt2sqlDate, dt2timestamp}
import edp.wormhole.flinkextension.util.ExtFlinkSchemaUtils.{object2TrueValue, s2TrueValue}
import org.apache.flink.api.common.typeinfo.{TypeInformation, Types}
import org.apache.flink.types.Row

abstract class AbstractPatternCondition(schemaMap: Map[String, (TypeInformation[_], Int)]) extends java.io.Serializable {

  protected def eventFilter(fieldName: String, value: String, compareType: String, event: Row): Boolean = {
    val rowFieldType = schemaMap(fieldName)._1
    val rowFieldValue = event.getField(schemaMap(fieldName)._2)
    val compareValue = if (null == value) null else value.trim
    val rowTrueValue = object2TrueValue(rowFieldType, rowFieldValue)
    val compareTrueValue = s2TrueValue(rowFieldType, value)

    if (rowFieldValue == null) false
    else CompareType.compareType(compareType) match {
      case CompareType.GREATERTHAN => rowFieldType match {
        case Types.STRING => rowFieldValue.asInstanceOf[String].trim > compareValue
        case Types.INT => rowFieldValue.asInstanceOf[Int] > compareValue.toInt
        case Types.LONG => rowFieldValue.asInstanceOf[Long] > compareValue.toLong
        case Types.FLOAT => rowFieldValue.asInstanceOf[Float] > compareValue.toFloat
        case Types.DOUBLE => rowFieldValue.asInstanceOf[Double] > compareValue.toDouble
        case Types.BIG_DEC => new java.math.BigDecimal(value).stripTrailingZeros().compareTo(new java.math.BigDecimal(compareValue).stripTrailingZeros()) > 0
        case Types.SQL_DATE => dt2sqlDate(value).compareTo(dt2sqlDate(compareValue)) > 0
        case Types.SQL_TIMESTAMP => dt2timestamp(value).compareTo(dt2timestamp(compareValue)) > 0
      }
      case CompareType.LESSTHAN => rowFieldType match {
        case Types.STRING => rowFieldValue.asInstanceOf[String].trim < compareValue
        case Types.INT => rowFieldValue.asInstanceOf[Int] < compareValue.toInt
        case Types.LONG => rowFieldValue.asInstanceOf[Long] < compareValue.toLong
        case Types.FLOAT => rowFieldValue.asInstanceOf[Float] < compareValue.toFloat
        case Types.DOUBLE => rowFieldValue.asInstanceOf[Double] < compareValue.toDouble
        case Types.BIG_DEC => new java.math.BigDecimal(value).stripTrailingZeros().compareTo(new java.math.BigDecimal(compareValue).stripTrailingZeros()) < 0
        case Types.SQL_DATE => dt2sqlDate(value).compareTo(dt2sqlDate(compareValue)) < 0
        case Types.SQL_TIMESTAMP => dt2timestamp(value).compareTo(dt2timestamp(compareValue)) < 0
      }
      case CompareType.GREATERTHANEQUALTO => rowFieldType match {
        case Types.STRING => rowFieldValue.asInstanceOf[String].trim >= compareValue
        case Types.INT => rowFieldValue.asInstanceOf[Int] >= compareValue.toInt
        case Types.LONG => rowFieldValue.asInstanceOf[Long] >= compareValue.toLong
        case Types.FLOAT => rowFieldValue.asInstanceOf[Float] >= compareValue.toFloat
        case Types.DOUBLE => rowFieldValue.asInstanceOf[Double] >= compareValue.toDouble
        case Types.BIG_DEC => new java.math.BigDecimal(value).stripTrailingZeros().compareTo(new java.math.BigDecimal(compareValue).stripTrailingZeros()) >= 0
        case Types.SQL_DATE => dt2sqlDate(value).compareTo(dt2sqlDate(compareValue)) >= 0
        case Types.SQL_TIMESTAMP => dt2timestamp(value).compareTo(dt2timestamp(compareValue)) >= 0
      }
      case CompareType.LESSTHANEQUALTO => rowFieldType match {
        case Types.STRING => rowFieldValue.asInstanceOf[String].trim <= compareValue
        case Types.INT => rowFieldValue.asInstanceOf[Int] <= compareValue.toInt
        case Types.LONG => rowFieldValue.asInstanceOf[Long] <= compareValue.toLong
        case Types.FLOAT => rowFieldValue.asInstanceOf[Float] <= compareValue.toFloat
        case Types.DOUBLE => rowFieldValue.asInstanceOf[Double] <= compareValue.toDouble
        case Types.BIG_DEC => new java.math.BigDecimal(value).stripTrailingZeros().compareTo(new java.math.BigDecimal(compareValue).stripTrailingZeros()) <= 0
        case Types.SQL_DATE => dt2sqlDate(value).compareTo(dt2sqlDate(compareValue)) <= 0
        case Types.SQL_TIMESTAMP => dt2timestamp(value).compareTo(dt2timestamp(compareValue)) <= 0
      }
      case CompareType.EQUALTO => rowTrueValue == compareTrueValue
      case CompareType.NOTEQUALTO => rowTrueValue != compareTrueValue
      case CompareType.LIKE => rowFieldValue.asInstanceOf[String].contains(compareValue)
      case CompareType.STARTWITH => rowFieldValue.asInstanceOf[String].startsWith(compareValue.asInstanceOf[String])
      case CompareType.ENDWITH => rowFieldValue.asInstanceOf[String].endsWith(compareValue.asInstanceOf[String])
    }
  }
}
