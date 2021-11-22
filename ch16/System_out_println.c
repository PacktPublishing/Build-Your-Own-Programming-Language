#include <stdio.h>
void System_out_println(struct String *s) {
  for(int i = 0; i < s->len; i++) putchar(s->buf[i]);
  putchar('\n');
}
