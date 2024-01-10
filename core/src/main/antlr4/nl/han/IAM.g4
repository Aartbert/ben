grammar IAM;

//--- LEXER ---

// ATTRIBUTES
HEALTH: 'hp' | 'levens' | 'gezondheid';
STAMINA: 'stamina' | 'energie' | 'uithoudingsvermogen' | 'doorzettingsvermogen';
POWER: 'strength' | 'power' | 'kracht';

// ITEMS
HEALTH_POTION: 'health potion' | 'health potions';

//OPERATIONS
OR: 'of';
AND: 'en';
GREATER_THAN: 'groter' | 'meer' | 'hoger' | 'groter is' | 'meer is' | 'hoger is';
LESS_THAN: 'kleiner' | 'minder' | 'lager' | 'binnen' | 'kleiner is' | 'minder is' | 'lager is' | 'binnen is';
IS_EQUAL: 'is';

//ACTIONS
MOVE: 'lopen' | 'bewegen' | 'verplaatsen' | 'loop' | 'beweeg' | 'verplaats' | 'loopt' | 'beweegt' | 'verplaatst';
PICK_UP: 'oppakken' | 'pak' | 'pakken' | 'oprapen' | 'oppikken' | 'vastpakken';
RETREAT: 'rennen' | 'wegrennen' | 'ren' | 'wegren' | 'rent' | 'wegrent';
WANDER: 'loop rond' | 'dwaal' | 'zwerf' | 'dwaalt' | 'zwerft';
ATTACK: 'val'| 'aanvallen';
USE: 'gebruik';

//CREATURES
ENEMY: 'vijand' | 'tegenstander';
PLAYER: 'speler';
MONSTER: 'monster';

// DIRECTIONS
UP: 'omhoog' | 'noord' | 'boven';
DOWN: 'omlaag'| 'zuid' | 'beneden';
LEFT: 'links' | 'west';
RIGHT: 'rechts' | 'oost';

// LITERALS
SCALAR: [0-9]+;
PERCENTAGE_SIGN: '%' | 'procent';
TRUE: 'waar' | 'wel';
FALSE: 'niet waar' | 'geen';

// OTHER
END_OF_SENTENCE: '.';
WS: [ \t\r\n]+ -> skip;
OTHER: [a-zA-Z0-9\-]+ -> skip;
COMMA: ',' -> skip;

//--- PARSER ---
configuration: sentence*;
sentence: ((expression? action) | (action expression?)) END_OF_SENTENCE;

expression: condition | binaryExpression;
binaryExpression: condition logicalOperator expression;
condition: comparison | existence;
comparison: (attribute comparisonOperator literal) | (attribute literal comparisonOperator) | (comparisonOperator literal attribute);
existence: (item comparisonOperator? literal?) | (literal? comparisonOperator? item);

action: operation | binaryOperation;
binaryOperation: operation logicalOperator action;
operation: movement | wander | attack | use | retreat | pickUp;
movement: (MOVE scalar? direction) | (direction scalar? MOVE);
attack: (ATTACK creature) | (creature ATTACK);
use: (USE item) | (item USE);
retreat: RETREAT;
wander: WANDER;
pickUp: PICK_UP;

direction: up | down | left | right;
attribute: health | stamina | power | creature;
comparisonOperator: greaterThanOperator | lessThanOperator | isEqualOperator | orOperator;
logicalOperator: andOperator;
literal: scalar | percentage | true | false;
item: healthPotion;
creature: player | monster | enemy;

// Lexer -> Parser rule
health: HEALTH;
stamina: STAMINA;
power: POWER;
healthPotion: HEALTH_POTION;
up: UP;
down: DOWN;
left: LEFT;
right: RIGHT;
scalar: SCALAR;
percentage: SCALAR PERCENTAGE_SIGN;
true: TRUE;
false: FALSE;
orOperator: OR;
andOperator: AND;
greaterThanOperator: GREATER_THAN;
lessThanOperator: LESS_THAN;
isEqualOperator: IS_EQUAL;
player: PLAYER;
monster: MONSTER;
enemy: ENEMY;
