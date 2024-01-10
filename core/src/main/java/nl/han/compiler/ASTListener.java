package nl.han.compiler;

import lombok.Getter;
import lombok.Setter;
import nl.han.IAMBaseListener;
import nl.han.IAMParser;
import nl.han.compiler.ast.Configuration;
import nl.han.compiler.ast.IASTNode;
import nl.han.compiler.ast.Sentence;
import nl.han.compiler.ast.actions.*;
import nl.han.compiler.ast.enums.Attribute;
import nl.han.compiler.ast.enums.CompilerItem;
import nl.han.compiler.ast.enums.Direction;
import nl.han.compiler.ast.expressions.BinaryExpression;
import nl.han.compiler.ast.expressions.Comparison;
import nl.han.compiler.ast.expressions.Existence;
import nl.han.compiler.ast.literals.Bool;
import nl.han.compiler.ast.literals.Percentage;
import nl.han.compiler.ast.literals.Scalar;
import nl.han.compiler.ast.operators.*;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * This class is responsible for building an AST.
 */
@Getter
@Setter
public class ASTListener extends IAMBaseListener {

    private Configuration ast = new Configuration();
    private Deque<IASTNode> stack = new ArrayDeque<>();
    private Set<ParserRuleContext> visitors = new HashSet<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterConfiguration(IAMParser.ConfigurationContext ctx) {
        enter(ast, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterSentence(IAMParser.SentenceContext ctx) {
        enter(new Sentence(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterBinaryExpression(IAMParser.BinaryExpressionContext ctx) {
        enter(new BinaryExpression(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterComparison(IAMParser.ComparisonContext ctx) {
        enter(new Comparison(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterExistence(IAMParser.ExistenceContext ctx) {
        enter(new Existence(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterBinaryOperation(IAMParser.BinaryOperationContext ctx) {
        enter(new BinaryOperation(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMovement(IAMParser.MovementContext ctx) {
        enter(new Movement(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterAttack(IAMParser.AttackContext ctx) {
        enter(new Attack(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterUse(IAMParser.UseContext ctx) {
        enter(new Use(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterRetreat(IAMParser.RetreatContext ctx) {
        enter(new Retreat(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterWander(IAMParser.WanderContext ctx) {
        enter(new Wander(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPickUp(IAMParser.PickUpContext ctx) {
        enter(new PickUp(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterUp(IAMParser.UpContext ctx) {
        enter(Direction.UP, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterDown(IAMParser.DownContext ctx) {
        enter(Direction.DOWN, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLeft(IAMParser.LeftContext ctx) {
        enter(Direction.LEFT, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterRight(IAMParser.RightContext ctx) {
        enter(Direction.RIGHT, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterHealth(IAMParser.HealthContext ctx) {
        enter(Attribute.HEALTH, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterStamina(IAMParser.StaminaContext ctx) {
        enter(Attribute.STAMINA, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPower(IAMParser.PowerContext ctx) {
        enter(Attribute.POWER, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterAndOperator(IAMParser.AndOperatorContext ctx) {
        enter(new AndOperator(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterGreaterThanOperator(IAMParser.GreaterThanOperatorContext ctx) {
        enter(new GreaterThanOperator(), ctx);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void enterIsEqualOperator(IAMParser.IsEqualOperatorContext ctx) {
        enter(new IsEqualOperator(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterLessThanOperator(IAMParser.LessThanOperatorContext ctx) {
        enter(new LessThanOperator(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterOrOperator(IAMParser.OrOperatorContext ctx) {
        enter(new OrOperator(), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterScalar(IAMParser.ScalarContext ctx) {
        enter(new Scalar(ctx.getText()), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPercentage(IAMParser.PercentageContext ctx) {
        enter(new Percentage(ctx.getText()), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterTrue(IAMParser.TrueContext ctx) {
        enter(new Bool(true), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterFalse(IAMParser.FalseContext ctx) {
        enter(new Bool(false), ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterHealthPotion(IAMParser.HealthPotionContext ctx) {
        enter(CompilerItem.HEALTH_POTION, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterMonster(IAMParser.MonsterContext ctx) {
        enter(Attribute.MONSTER, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterPlayer(IAMParser.PlayerContext ctx) {
        enter(Attribute.PLAYER, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enterEnemy(IAMParser.EnemyContext ctx) {
        enter(Attribute.ENEMY, ctx);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        if (!visitors.contains(ctx)) return;

        visitors.remove(ctx);
        stack.pop();
    }

    /**
     * Adds a node as a child to the ASTNode that is at the peek of the stack that is stored in the ASTListener
     * that this method is called upon. If the stack is empty the node will be added as a child to the root of the
     * AST that is stored in the ASTListener that this method was called upon. Afterwards this method adds the new
     * child node to the stack.
     *
     * @param node The ASTNode representing the context being entered.
     */
    private void enter(IASTNode node, ParserRuleContext ctx) {
        if (!stack.isEmpty()) stack.peek().addChild(node);

        visitors.add(ctx);
        stack.push(node);
    }
}

