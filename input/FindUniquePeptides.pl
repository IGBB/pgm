#! /usr/bin/perl

use strict;
use IO::File;

if($#ARGV + 1 != 2) {
	print "usage: perl FindUniquePeptides.pl <inputFile> <outputFile>\n";
	exit(0);
}

my $inputFile = new IO::File($ARGV[0], "r") or die $!;
my $outputFile = new IO::File($ARGV[1], "w") or die $!;

# assume the input lines are of the form <sequence>\t<probability>

# output <sequence>\t<probability>\t<count>
# for each unique sequence
# probability is (1-p) for each sequence

my %probability;
my %count;

while(my $line = $inputFile->getline) {
	chomp($line);
	my @split = split("\t", $line);

	my $sequence = $split[0];
	my $p = $split[1];

	if (exists $probability{$sequence}) {
		my $newProbability = $probability{$sequence} * (1-$p);
		my $newCount = $count{$sequence} + 1;
		
		$probability{$sequence} = $newProbability;
		$count{$sequence} = $newCount;
	} else {
		$probability{$sequence} = $p;
		$count{$sequence} = 1;
	}
}

while ( my ($sequence, $p) = each(%probability) ) {
	my $c = $count{$sequence};
	print $outputFile "$sequence\t$p\t$c\n";
}

$inputFile->close;
$outputFile->close;
