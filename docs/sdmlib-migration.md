# Migrating from SDMLib

SDMLib syntax can be migrated to fulib decorator syntax with a few regular expressions:

> The examples assume a `ClassModelManager model` parameter.

```regexp
(\w+).(with|create)Attribute\(
model.haveAttribute($1, 

(\w+).(with|create)UniDirectional\((\w+), "(\w+)", (ONE|MANY)\)
model.associate($1, "$3", $4, $2, null, 0)

(\w+).(with|create)Bidirectional\((\w+), "(\w+)", (ONE|MANY), "(\w+)", (ONE|MANY)\)
model.associate($1, "$3", $4, $2, "$5", $6)

\)\.withSuperClazz\(
, 
```
