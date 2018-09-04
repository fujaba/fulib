
attrSet(class, name, type) ::= <<
   public <class> set<name; format="cap">(<type> value)
   {
      this.<name> = value;
      return this;
   }
>>