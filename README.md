# marketing
构建营销平台，是各个互联网公司用于拉新、促活、留存、转化的重要手段。本项目主要在于理解此类项目的系统设计及DDD架构的实现。

---

### 20241011

#### 在权重规则下执行抽奖

1. 执行权重规则的策略的奖池不同，所以自动装配策略时也要装配配置权重规则后的策略
2. 此时出现多个奖池，不同类型的用户会进入不同的奖池，所以需要重载获取奖品方法

### 20241012

#### 抽奖接口+前置规则兼容+抽奖策略+实体类构建

1. 抽奖接口，提供策略ID+用户名参数（当前功能可测试到的参数，后续可能继续添加）。
   1. 查询策略，获取该策略的规则
   2. 通过规则工厂获取规则处理器并处理，当前只有两种简单的前置规则，所以处理方法就是有黑名单直接执行黑名单，没有则继续看是否有权重规则，然后处理。
   3. 执行抽奖
2. 前置规则兼容
   1. 创建规则处理接口，不同规则各自实现
   2. 创建工厂装载规则处理接口实现类。通过自定义注解扫描所有规则处理接口实现类
   3. 抽奖时可从工厂中获取需要的规则处理接口实现类
3. 抽奖策略
   1. 创建抽奖策略抽象类，统一抽奖方法
   2. 创建前置规则处理的抽象接口，创建子类实现该接口。可以创建多个以实现不同的处理，比如先执行黑名单规则，再执行权重规则等等
4. 实体类构建
   1. 就像 Spring 源码中拆解一个 Bean 对象为不同阶段一样，我们这里也把抽奖拆解为不同时间段的过程，以用于可以在各个节点添加所需的功能流程。所以就可以将`RuleActionEntity`对象在内部区分为多个阶段的对象，方便做不同的处理且减少不必要的代码。

### 20241013

```
预：抽奖n次后解锁功能 branch:20241014-raffling-unlock-precode
1. 执行某策略的抽奖
2. 查询该策略下的次数解锁处理器
3. 查询该策略下需要解锁的奖品
4. 查询该策略下兜底奖品列表
5. 查询当前抽奖次数，如果+1 < 抽奖次数，则返回兜底奖品
   1. 如何区分已解锁和未解锁的奖品？
      不存在是否解锁的奖品，奖池的奖品包括策略下的所有奖品，抽到未解锁的奖品时只需要返回
      兜底奖品即可，库存没了同理。
6. 如果+1 >= 抽奖次数，查看奖品库存，如果库存不够，则返回兜底奖品
```
#### 抽奖中规则实现

1. 根据已有代码来看，判断一个奖品是否需要解锁是一个单独的功能，或者说步骤。仅需要实现ILogicFilter接口，然后判断接管还是放行，不需要将兜底处理耦合进ILogicFilter中
2. 抽奖中规则过滤实际上是对抽奖前规则处理后抽出的奖品的处理，所以需要修改或新增部分对象来兼容功能

#### 责任链工厂
抽奖前规则的处理耦合度仍然较高，仍然需要在一个代码块中进行分类处理。为了优化此代码，使用责任链工厂来处理。
```
     if (ruleActionEntity != null && RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())) {
         if (DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())) {
             // 黑名单返回固定奖品ID
             return RaffleAwardEntity.builder()
                     .awardId(ruleActionEntity.getData().getAwardId())
                     .build();
         } else if (DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode().equals(ruleActionEntity.getRuleModel())) {
             // 权重根据返回信息抽奖
             RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
             String ruleWeightValueKey = raffleBeforeEntity.getRuleWeightValueKey();
             Integer randomAwardId = strategyDispatch.getRandomAwardId(strategyId, ruleWeightValueKey);
             return RaffleAwardEntity.builder()
                     .awardId(randomAwardId)
                     .build();
         }
     }
```

1. 创建型接口，用于链接责任链节点
```java
   public interface ILogicChainArmory {
   
       ILogicChain appendNext(ILogicChain next);
   
       ILogicChain next();
   }
```
2. 规则实现接口
```java
   public interface ILogicChain extends ILogicChainArmory {
   
       /**
        * 责任链接口
        * @param userId 用户ID
        * @param strategyId 策略ID
        * @return 奖品ID
        */
       Integer logic(String userId, Long strategyId);
   
   
   }
```
3. 责任链工厂
```java
   public class DefaultChainFactory {
      
       private final Map<String, ILogicChain> logicChainGroup;
      
       private IStrategyRepository strategyRepository;
      
       public DefaultChainFactory(IStrategyRepository strategyRepository, Map<String, ILogicChain> logicChainGroup) {
           this.logicChainGroup = logicChainGroup;
           this.strategyRepository = strategyRepository;
       }
      
       public ILogicChain openLogicChain(Long strategyId) {
           StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);
           String[] ruleModels = strategyEntity.getRuleModels();
      
           if (ruleModels == null || ruleModels.length == 0) {
               return logicChainGroup.get("default");
           }
      
           ILogicChain logicChain = logicChainGroup.get(ruleModels[0]);
           ILogicChain current = logicChain;
      
           for (int i = 1; i < ruleModels.length; i++) {
               current = current.appendNext(logicChainGroup.get(ruleModels[i]));
           }
      
           current.appendNext(logicChainGroup.get("default"));
      
           return logicChain;
       }
   }
```
   1. 通过构造器注入策略仓储，以及规则链的实现类Map
   2. 规则类的实现类Map由Spring自动装载：Spring会将一个接口的实现类自动组装成一个Map<name, impl>并管理起来
   3. openLogicChain(Long strategyId)方法根据仓储查询出来的ruleModels，获取规则列表并组装对应的规则链
4. 抽奖前规则和抽奖中的规则不同，所以其处理也不同，并不能直接兼容进规则链，待后续处理。
