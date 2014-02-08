package ao.simple.alexo.state;

/**
 *
 */
public enum AlexoRoundState
{
    FIRST_ACT,

    CHECK,
    CHECK_CHECK, //
    CHECK_FOLD,  //
    CHECK_BET,
    CHECK_BET_FOLD, //
    CHECK_BET_CALL, //
    CHECK_BET_RAISE,
    CHECK_BET_RAISE_FOLD, //
    CHECK_BET_RAISE_CALL, //
    
    BET,
    BET_FOLD, //
    BET_CALL,
    BET_RAISE,
    BET_RAISE_FOLD, //
    BET_RAISE_CALL, //
    
    FOLD //
}
