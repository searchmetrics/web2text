#!/usr/bin/perl

package Victor::Cz::EncoDetect;
use Exporter;
@ISA = qw (Exporter);
@EXPORT = qw (&detects);

use strict;
use warnings;
use utf8;
use Encode;

###########################################################################################################################
# inicializace poli
###########################################################################################################################
my @cp1250;
for (my $i = 128; $i < 256; $i++) { $cp1250[$i] = 0; }

$cp1250[138] = 1;  $cp1250[141] = 1;  $cp1250[142] = 1;  $cp1250[154] = 1;
$cp1250[157] = 1;  $cp1250[158] = 1;  $cp1250[193] = 1;  $cp1250[200] = 1;
$cp1250[201] = 1;  $cp1250[204] = 1;  $cp1250[205] = 1;  $cp1250[207] = 1;
$cp1250[210] = 1;  $cp1250[211] = 1;  $cp1250[216] = 1;  $cp1250[217] = 1;
$cp1250[218] = 1;  $cp1250[221] = 1;  $cp1250[225] = 1;  $cp1250[232] = 1;
$cp1250[233] = 1;  $cp1250[236] = 1;  $cp1250[237] = 1;  $cp1250[239] = 1;
$cp1250[242] = 1;  $cp1250[243] = 1;  $cp1250[248] = 1;  $cp1250[249] = 1;
$cp1250[250] = 1;  $cp1250[253] = 1;

my @i88592;
for (my $i = 128; $i < 256; $i++) { $i88592[$i] = 0; }

$i88592[169] = 1;  $i88592[171] = 1;  $i88592[174] = 1;  $i88592[185] = 1;
$i88592[187] = 1;  $i88592[190] = 1;  $i88592[193] = 1;  $i88592[200] = 1;
$i88592[201] = 1;  $i88592[204] = 1;  $i88592[205] = 1;  $i88592[207] = 1;
$i88592[210] = 1;  $i88592[211] = 1;  $i88592[216] = 1;  $i88592[217] = 1;
$i88592[218] = 1;  $i88592[221] = 1;  $i88592[225] = 1;  $i88592[232] = 1;
$i88592[233] = 1;  $i88592[236] = 1;  $i88592[237] = 1;  $i88592[239] = 1;
$i88592[242] = 1;  $i88592[243] = 1;  $i88592[248] = 1;  $i88592[249] = 1;
$i88592[250] = 1;  $i88592[253] = 1; 
###########################################################################################################################
###########################################################################################################################

sub detects ($) {
  my ($input) = @_;
  my $i;
  ($input) or die "Write at least one argument (input text).\n";

  my $max = length($input);
  my $error = 0;
  my $count = 0;
  for (my $j = 0; $j < $max; $j++ ) {
    $i = substr($input, $j, 1);
    if ($count > 0) { $count--; }
    else {
      if (ord($i) > 253) { next; }
      if (ord($i) > 251) { $count = 5; next; }
      if (ord($i) > 247) { $count = 4; next; }
      if (ord($i) > 239) { $count = 3; next; }
      if (ord($i) > 223) { $count = 2; next; }
      if (ord($i) > 191) { $count = 1; next; }
      if (ord($i) > 127) { $error = 1; last; }
    } 
  }
  close INPUT;  
  if ($error + $count == 0) { return "utf8"; }
  
  my ($cp, $iso) = (0, 0);
  for (my $j = 0; $j < $max; $j++ ) {
    $i = substr($input, $j, 1);
    if (ord($i) > 127) {
      if ($cp1250[ord($i)] == 1) { $cp++; }
      if ($i88592[ord($i)] == 1) { $iso++; }
    }  
  }
  close INPUT;  
  return ($cp > $iso) ? "cp1250" : "iso-8859-2";
}

1;