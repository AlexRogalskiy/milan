package com.amazon.milan.program.internal

import com.amazon.milan.program
import com.amazon.milan.program._
import com.amazon.milan.typeutil.LiftTypeDescriptorHost

import scala.reflect.macros.whitebox


/**
 * Trait enabling macro bundles to lift Milan program elements into expression trees.
 */
trait LiftableImpls extends ProgramTypeNamesHost with LiftTypeDescriptorHost {
  val c: whitebox.Context

  import c.universe._

  implicit val liftDuration: Liftable[Duration] = { t => q"new ${typeOf[Duration]}(${t.milliseconds})" }

  implicit val liftFunctionReference: Liftable[FunctionReference] = { t => q"new ${typeOf[FunctionReference]}(${t.objectTypeName}, ${t.functionName})" }

  // Below are Liftables for all of the expression tree types.
  // We also need Liftables for any base types or traits of expression tree types, like AggregateExpression, because
  // sometimes that's the type of reference we have.

  implicit val liftApplyFunction: Liftable[ApplyFunction] = { t => q"new ${typeOf[ApplyFunction]}(${t.function}, ${t.args}, ${t.tpe})" }

  implicit val liftConstantValue: Liftable[ConstantValue] = { t => q"new ${typeOf[ConstantValue]}(${Constant(t.value)}, ${t.tpe})" }

  implicit val liftConvertType: Liftable[ConvertType] = { t => q"new ${typeOf[ConvertType]}(${t.expr}, ${t.tpe})" }

  implicit val liftCreateInstance: Liftable[CreateInstance] = { t => q"new ${typeOf[CreateInstance]}(${t.ty}, ${t.args})" }

  implicit val liftValueDef: Liftable[ValueDef] = { t => if (t.tpe != null) q"new ${typeOf[ValueDef]}(${t.name}, ${t.tpe})" else q"new ${typeOf[ValueDef]}(${t.name})" }

  implicit val liftFunctionDef: Liftable[FunctionDef] = { t => q"new ${typeOf[FunctionDef]}(${t.arguments}, ${t.body})" }

  implicit val liftIsNull: Liftable[IsNull] = { t => q"new ${typeOf[IsNull]}(${t.expr})" }

  implicit val liftMinus: Liftable[Minus] = { t => q"new ${typeOf[Minus]}(${t.left}, ${t.right})" }

  implicit val liftNot: Liftable[Not] = { t => q"new ${typeOf[Not]}(${t.expr})" }

  implicit val liftPlus: Liftable[Plus] = { t => q"new ${typeOf[Plus]}(${t.left}, ${t.right})" }

  implicit val liftSelectField: Liftable[SelectField] = { t => q"new ${typeOf[SelectField]}(${t.qualifier}, ${t.fieldName})" }

  implicit val liftSelectTerm: Liftable[SelectTerm] = { t => q"new ${typeOf[SelectTerm]}(${t.termName})" }

  implicit val liftTupleElement: Liftable[TupleElement] = { t => q"new ${typeOf[TupleElement]}(${t.target}, ${t.index})" }

  implicit val liftSelectExpression: Liftable[SelectExpression] = { tree =>
    tree match {
      case t: SelectTerm => liftSelectTerm(t)
      case t: SelectField => liftSelectField(t)
    }
  }

  implicit val liftIfThenElse: Liftable[IfThenElse] = { t => q"new ${typeOf[IfThenElse]}(${t.condition}, ${t.thenExpr}, ${t.elseExpr})" }

  implicit val liftTuple: Liftable[Tuple] = { t => q"new ${typeOf[Tuple]}(${t.elements})" }

  implicit val liftUnpack: Liftable[Unpack] = { t => q"new ${typeOf[Unpack]}(${t.target}, ${t.names}, ${t.body})" }

  implicit val liftAnd: Liftable[And] = { t => q"new ${typeOf[And]}(${t.left}, ${t.right})" }
  implicit val liftEquals: Liftable[Equals] = { t => q"new ${typeOf[Equals]}(${t.left}, ${t.right})" }
  implicit val liftGreaterThan: Liftable[GreaterThan] = { t => q"new ${typeOf[GreaterThan]}(${t.left}, ${t.right})" }
  implicit val liftGreaterThanOrEqual: Liftable[GreaterThanOrEqual] = { t => q"new ${typeOf[GreaterThanOrEqual]}(${t.left}, ${t.right})" }
  implicit val liftLessThan: Liftable[LessThan] = { t => q"new ${typeOf[LessThan]}(${t.left}, ${t.right})" }
  implicit val liftLessThanOrEqual: Liftable[LessThanOrEqual] = { t => q"new ${typeOf[LessThanOrEqual]}(${t.left}, ${t.right})" }

  implicit val liftBinaryLogicalOperator: Liftable[BinaryLogicalOperator] = { tree =>
    tree match {
      case t: And => liftAnd(t)
      case t: Equals => liftEquals(t)
      case t: GreaterThan => liftGreaterThan(t)
      case t: LessThan => liftLessThan(t)
    }
  }

  implicit val liftCount: Liftable[Count] = { t => q"new ${typeOf[Count]}()" }
  implicit val liftSum: Liftable[Sum] = { t => q"new ${typeOf[Sum]}(${t.expr})" }
  implicit val liftMin: Liftable[Min] = { t => q"new ${typeOf[Min]}(${t.expr})" }
  implicit val liftMax: Liftable[Max] = { t => q"new ${typeOf[Max]}(${t.expr})" }
  implicit val liftFirst: Liftable[First] = { t => q"new ${typeOf[First]}(${t.expr})" }
  implicit val liftMean: Liftable[Mean] = { t => q"new ${typeOf[Mean]}(${t.expr})" }
  implicit val liftArgMin: Liftable[ArgMin] = { t => q"new ${typeOf[ArgMin]}(${t.expr})" }
  implicit val liftArgMax: Liftable[ArgMax] = { t => q"new ${typeOf[ArgMax]}(${t.expr})" }

  implicit val liftAggregateExpression: Liftable[AggregateExpression] = { tree =>
    tree match {
      case t: Count => liftCount(t)
      case t: Sum => liftSum(t)
      case t: Min => liftMin(t)
      case t: Max => liftMax(t)
      case t: First => liftFirst(t)
      case t: Mean => liftMean(t)
      case t: ArgMin => liftArgMin(t)
      case t: ArgMax => liftArgMax(t)
    }
  }

  implicit val liftStreamMap: Liftable[StreamMap] = { t => q"new ${typeOf[StreamMap]}(${t.source}, ${t.expr}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftFlatMap: Liftable[FlatMap] = { t => q"new ${typeOf[FlatMap]}(${t.source}, ${t.expr}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftLeftJoin: Liftable[LeftJoin] = { t => q"new ${typeOf[LeftJoin]}(${t.left}, ${t.right}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftFullJoin: Liftable[FullJoin] = { t => q"new ${typeOf[FullJoin]}(${t.left}, ${t.right}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }

  implicit val liftJoinNodeExpression: Liftable[JoinExpression] = { tree =>
    tree match {
      case t: LeftJoin => liftLeftJoin(t)
      case t: FullJoin => liftFullJoin(t)
    }
  }

  implicit val liftGroupBy: Liftable[GroupBy] = { t => q"new ${typeOf[GroupBy]}(${t.source}, ${t.expr}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftTumblingWindow: Liftable[TumblingWindow] = { t => q"new ${typeOf[TumblingWindow]}(${t.source}, ${t.period}, ${t.offset}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftSlidingWindow: Liftable[SlidingWindow] = { t => q"new ${typeOf[SlidingWindow]}(${t.source}, ${t.size}, ${t.slide}, ${t.offset}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }

  implicit val liftGroupingExpression: Liftable[GroupingExpression] = { tree =>
    tree match {
      case t: GroupBy => liftGroupBy(t)
      case t: TumblingWindow => liftTumblingWindow(t)
      case t: SlidingWindow => liftSlidingWindow(t)
    }
  }

  implicit val liftStreamArgMax: Liftable[StreamArgMax] = { t => q"new ${typeOf[StreamArgMax]}(${t.source}, ${t.argExpr}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftStreamArgMin: Liftable[StreamArgMin] = { t => q"new ${typeOf[StreamArgMin]}(${t.source}, ${t.argExpr}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }

  implicit val liftArgCompareExpression: Liftable[ArgCompareExpression] = { tree =>
    tree match {
      case t: StreamArgMax => liftStreamArgMax(t)
      case t: StreamArgMin => liftStreamArgMin(t)
    }
  }

  implicit val liftScanExpression: Liftable[ScanExpression] = { tree =>
    tree match {
      case t: ArgCompareExpression => liftArgCompareExpression(t)
    }
  }

  implicit val liftRef: Liftable[Ref] = { t => q"new ${typeOf[Ref]}(${t.nodeId}, ${t.nodeName})" }
  implicit val liftFilter: Liftable[Filter] = { t => q"new ${typeOf[Filter]}(${t.source}, ${t.predicate}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftLast: Liftable[Last] = { t => q"new ${typeOf[Last]}(${t.source}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }
  implicit val liftLeftWindowedJoin: Liftable[LeftWindowedJoin] = { t => q"new ${typeOf[LeftWindowedJoin]}(${t.left}, ${t.right}, ${t.nodeId}, ${t.nodeName}, ${t.tpe})" }

  implicit val liftStreamExpression: Liftable[StreamExpression] = { tree =>
    tree match {
      case t: Filter => liftFilter(t)
      case t: FlatMap => liftFlatMap(t)
      case t: StreamMap => liftStreamMap(t)
      case t: JoinExpression => liftJoinNodeExpression(t)
      case t: GroupingExpression => liftGroupingExpression(t)
      case t: Ref => liftRef(t)
      case t: Last => liftLast(t)
      case t: ScanExpression => liftScanExpression(t)
      case t: LeftWindowedJoin => liftLeftWindowedJoin(t)
    }
  }

  implicit val liftTree: Liftable[program.Tree] = { tree =>
    tree match {
      case t: AggregateExpression => liftAggregateExpression(t)
      case t: ApplyFunction => liftApplyFunction(t)
      case t: BinaryLogicalOperator => liftBinaryLogicalOperator(t)
      case t: ConstantValue => liftConstantValue(t)
      case t: ConvertType => liftConvertType(t)
      case t: CreateInstance => liftCreateInstance(t)
      case t: Duration => liftDuration(t)
      case t: FunctionDef => liftFunctionDef(t)
      case t: IfThenElse => liftIfThenElse(t)
      case t: IsNull => liftIsNull(t)
      case t: Minus => liftMinus(t)
      case t: Not => liftNot(t)
      case t: Plus => liftPlus(t)
      case t: SelectExpression => liftSelectExpression(t)
      case t: StreamExpression => liftStreamExpression(t)
      case t: Tuple => liftTuple(t)
      case t: TupleElement => liftTupleElement(t)
      case t: Unpack => liftUnpack(t)
    }
  }
}
