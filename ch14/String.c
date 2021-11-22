struct String {
  struct Class *cls;
  long len;
  char *buf;
};

struct String *j0concat(struct String *s1, struct String *s2){
  struct string *s3 = alloc(sizeof struct String);
  s3->buf = allocstring(s1->len + s2->len);
  strncpy(s3->buf, s1->buf, s1->len);
  strncpy(s3->buf + s1->len, s2->buf, s2->len);
  return s3;
}
